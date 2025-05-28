package live.narcy.weather.weatherApi.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import live.narcy.weather.weatherApi.dto.WeatherDetailsDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class WeatherApiService {

    @Value("${open.weather.api.key}")
    private String apiKey;
    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate;

    public WeatherApiService(ObjectMapper objectMapper, RestTemplate restTemplate) {
        this.objectMapper = objectMapper;
        this.restTemplate = restTemplate;
    }

    /**
     * 위도, 경도 조회 후 날씨 조회
     * @param city
     * @return ResponseEntity<Map<String, List<WeatherDetailsDTO>>>
     */
    public ResponseEntity<Map<String, List<WeatherDetailsDTO>>> getWeather(String city) {
        String geocodeUrl = UriComponentsBuilder
                .fromUriString("https://api.openweathermap.org/geo/1.0/direct")
                .queryParam("q", city)                   // 도시명
                .queryParam("limit", 1)        // 응답개수
                .queryParam("appid", apiKey)
                .build()
                .toString();

        try {
            ResponseEntity<String> locationEntity = restTemplate.getForEntity(geocodeUrl, String.class);
            if(locationEntity.getStatusCode() != HttpStatus.OK || locationEntity.getBody() == null) {
                System.out.println("위치 정보를 찾을 수 없습니다.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }

            JsonNode locationArray = objectMapper.readTree(locationEntity.getBody());
            if(locationArray.isEmpty()) {
                System.out.println("위치 정보를 찾을 수 없습니다.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }

            JsonNode locationJson = locationArray.get(0);
            double lat = locationJson.get("lat").asDouble();
            double lon = locationJson.get("lon").asDouble();

            // 주간 날씨 조회 API 호출
            Map<String, List<WeatherDetailsDTO>> weeklyWeather = getWeeklyWeather(lat, lon);

            // 현재 날씨 조회 API 호출
            WeatherDetailsDTO currentWeather = getCurrentWeather(lat, lon);

            // 현재 날씨 정보 Map<String, List<WeatherDetailsDTO>> 에 추가
            weeklyWeather.put("now", Collections.singletonList(currentWeather));

            // 응답 코드와 함께 날씨 정보 반환
            return ResponseEntity.ok(weeklyWeather);
        } catch (Exception e) {
            System.out.println("날씨 정보를 가져오는 데 실패했습니다.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 주간 날씨 조회 API 호출
     * @param lat
     * @param lon
     * @return Map<String, List<WeatherDetailsDTO>>
     * @throws JsonProcessingException
     */
    public Map<String, List<WeatherDetailsDTO>> getWeeklyWeather(double lat, double lon) throws JsonProcessingException {
        String fiveDayWeatherUrl = UriComponentsBuilder
                .fromUriString("https://api.openweathermap.org/data/2.5/forecast")
                .queryParam("lat", lat)                      // 위도
                .queryParam("lon", lon)                      // 경도
                .queryParam("lang", "kr")           // 언어
                .queryParam("appid", apiKey)
                .queryParam("units", "metric")      // 온도 단위
                .build()
                .toString();

        String weatherInfo = restTemplate.getForObject(fiveDayWeatherUrl, String.class);    // API 응답
//        System.out.println("weatherInfo = " + weatherInfo);

        JsonNode node = objectMapper.readTree(weatherInfo);
        JsonNode listNode = node.get("list");

        Map<String, List<WeatherDetailsDTO>> weatherDetails = new LinkedHashMap<>();

        for(JsonNode dayNode : listNode) {
            ObjectNode dayObject = (ObjectNode) dayNode;
            JsonNode utcTimeNode = dayNode.get("dt_txt");
//            System.out.println("utcTimeNode1 = " + utcTimeNode);

            String utcTimeText = utcTimeNode.asText();  // asText() : ""(쌍따옴표) 제거 후 문자열
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime dateTime = LocalDateTime.parse(utcTimeText, formatter);

            // UTC 시간대를 지정한 ZonedDateTime 생성
            ZonedDateTime utcTime = dateTime.atZone(ZoneId.of("UTC"));

            // 한국시간(ISO -> KST) = UCT -> UCT+9
            ZonedDateTime kstTime = utcTime.withZoneSameInstant(ZoneId.of("Asia/Seoul"));

            // 날씨정보 객체(dto) 생성
            WeatherDetailsDTO weatherDetailsDTO = WeatherDetailsDTO.builder()
                            .temp(dayObject.get("main").get("temp").asDouble())
                                    .description(dayObject.get("weather").get(0).get("description").asText())
                                            .icon(dayObject.get("weather").get(0).get("icon").asText())
                                                    .date(kstTime.toLocalDateTime()).build();

            // 해당 날짜의 리스트에 항목을 추가 (리스트가 없으면 새로 만듦)
            weatherDetails
                    .computeIfAbsent(kstTime.toLocalDate().toString(), k -> new ArrayList<>())
                    .add(weatherDetailsDTO);
        }

//        for (Map.Entry<String, List<WeatherDetailsDTO>> entry : weatherDetails.entrySet()) {
//            System.out.println("Date = " + entry.getKey());
//            for (WeatherDetailsDTO dto : entry.getValue()) {
//                System.out.println("DTO = " + dto);
//            }
//        }

//        System.out.println("updatedJson = " + objectMapper.writeValueAsString(listNode));

        return weatherDetails;
    }

    /**
     * 현재 날씨 조회 API 호출
     * @param lat
     * @param lon
     * @return WeatherDetailsDTO
     * @throws JsonProcessingException
     */
    public WeatherDetailsDTO getCurrentWeather(double lat, double lon) throws JsonProcessingException {
        String currentWeatherUrl = UriComponentsBuilder
                .fromUriString("https://api.openweathermap.org/data/2.5/weather")
                .queryParam("lat", lat)                     // 위도
                .queryParam("lon", lon)                     // 경도
                .queryParam("lang", "kr")          // 언어
                .queryParam("appid", apiKey)
                .queryParam("units", "metric")     // 온도 단위
                .build()
                .toString();

        String weatherInfo = restTemplate.getForObject(currentWeatherUrl, String.class);        // API 응답 데이터
//        System.out.println("weatherInfo = " + weatherInfo);

        JsonNode node = objectMapper.readTree(weatherInfo);
        JsonNode currentTemp = node.get("main").get("temp");
        JsonNode currentWeather = node.get("weather").get(0);
        JsonNode description = currentWeather.get("description");
        JsonNode icon = currentWeather.get("icon");

        // 현재 날씨 정보 반환
        return WeatherDetailsDTO.builder()
                        .temp(currentTemp.asDouble())
                                .description(description.asText())
                                        .icon(icon.asText())
                                            .date(LocalDateTime.now())
                                                .build();
    }
}
