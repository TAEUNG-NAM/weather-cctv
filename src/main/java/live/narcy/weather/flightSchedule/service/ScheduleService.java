package live.narcy.weather.flightSchedule.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import live.narcy.weather.flightSchedule.dto.AvailableAirlineDTO;
import live.narcy.weather.flightSchedule.dto.AvailableDestinationsDTO;
import live.narcy.weather.flightSchedule.dto.FlightSchedulesDTO;
import live.narcy.weather.flightSchedule.dto.IncheonAvailableDestinations;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate;


    /**
     * 출발지 체크 후 도착지 목록 조회
     * @param param
     * @return
     * @throws JsonProcessingException
     */
    public List<AvailableDestinationsDTO> getDestinations(String param) throws JsonProcessingException {
        JsonNode jsonNode = objectMapper.readTree(param);
        List<AvailableDestinationsDTO> availableDestinationsList;

        String pAirport = jsonNode.get("pAirport").asText();
        if("ICN".equals(pAirport)) {
            availableDestinationsList = getIncheonAvailableDestinations();
        } else {
            availableDestinationsList = getAvailableDestinations(jsonNode);
        }

        return availableDestinationsList;
    }

    /**
     * 출발 공항에서 갈 수있는 도착지 조회
     * @param jsonNode
     * @return
     * @throws JsonProcessingException
     */
    public List<AvailableDestinationsDTO> getAvailableDestinations(JsonNode jsonNode) throws JsonProcessingException {
        String pSelType = jsonNode.get("pSelType").asText();
        String pFindDate = jsonNode.get("pFindDate").asText();
        String pAirport = jsonNode.get("pAirport").asText();
        String pAirline = jsonNode.get("pAirline").asText();
        String pIotype = jsonNode.get("pIotype").asText();
        String pAgent = jsonNode.get("pAgent").asText();
        String pFightNum = jsonNode.get("pFightNum").asText();

        String availableDestinationsURL = UriComponentsBuilder
                .fromUriString("https://www.airport.co.kr/gimhae/ajaxf/frFlightSchSvc/getAvailableAirportInte.do")
                .queryParam("pSelType", pSelType)
                .queryParam("pFindDate", pFindDate)
                .queryParam("pAirport", pAirport)
                .queryParam("pAirline", pAirline)
                .queryParam("pIotype", pIotype)
                .queryParam("pAgent", pAgent)
                .queryParam("pFightNum", pFightNum)
                .build()
                .toString();


        String result = restTemplate.getForObject(availableDestinationsURL, String.class);
        JsonNode jsonNodeData = objectMapper.readTree(result).get("data");
        List<AvailableDestinationsDTO> availableDestinationsList = objectMapper.convertValue(jsonNodeData, new TypeReference<List<AvailableDestinationsDTO>>() {});

        return availableDestinationsList;
    }

    /**
     * 인천 공항에서 갈 수 있는 도착지 조회
     * @return
     */
    public List<AvailableDestinationsDTO> getIncheonAvailableDestinations() {
        IncheonAvailableDestinations[] destinations = IncheonAvailableDestinations.values();
        List<AvailableDestinationsDTO> availableDestinationsList = new ArrayList<>();

        for(IncheonAvailableDestinations destination : destinations) {
            AvailableDestinationsDTO destinationsDTO = AvailableDestinationsDTO.builder()
                    .cityCode(destination.name())
                    .cityKor(destination.getDestination())
                    .cityEng(destination.name())
                    .build();

            availableDestinationsList.add(destinationsDTO);
        }

        return availableDestinationsList;
    }

    /**
     * 출발 공항에서 이용 가능한 항공사 조회
     * @param param
     * @return
     * @throws JsonProcessingException
     */
    public List<AvailableAirlineDTO> getAvailableAirlines(String param) throws JsonProcessingException {
        JsonNode jsonNode = objectMapper.readTree(param);

        String pSelType = jsonNode.get("pSelType").asText();
        String pFindDate = jsonNode.get("pFindDate").asText();
        String pAirport = jsonNode.get("pAirport").asText();
        String pAirline = jsonNode.get("pAirline").asText();
        String pIotype = jsonNode.get("pIotype").asText();
        String pAgent = jsonNode.get("pAgent").asText();
        String pFightNum = jsonNode.get("pFightNum").asText();

        String availableAirlinesURL = UriComponentsBuilder
                .fromUriString("https://www.airport.co.kr/gimhae/ajaxf/frFlightSchSvc/getAirlineListInte.do")
                .queryParam("pSelType", pSelType)
                .queryParam("pFindDate", pFindDate)
                .queryParam("pAirport", pAirport)
                .queryParam("pAirline", pAirline)
                .queryParam("pIotype", pIotype)
                .queryParam("pAgent", pAgent)
                .queryParam("pFightNum", pFightNum)
                .queryParam("langCd", "ko")
                .build()
                .toString();

        String result = restTemplate.getForObject(availableAirlinesURL, String.class);
        JsonNode jsonNodeData = objectMapper.readTree(result).get("data");
        List<AvailableAirlineDTO> availableAirlinesList = objectMapper.convertValue(jsonNodeData, new TypeReference<List<AvailableAirlineDTO>>() {});

        return availableAirlinesList;
    }


    /**
     * 출발지&도착지 체크 후 운항스케줄 조회
     * @param param
     * @return
     * @throws JsonProcessingException
     */
    public List<FlightSchedulesDTO> getSchedules(String param) throws JsonProcessingException {
        JsonNode jsonNode = objectMapper.readTree(param);
        List<FlightSchedulesDTO> flightSchedulesList;

        String pAirport = jsonNode.get("pAirport").asText();
        if("ICN".equals(pAirport)) {
            flightSchedulesList = getIncheonFlightSchedules(jsonNode);
        } else {
            flightSchedulesList = getFlightSchedules(jsonNode);
        }

        return flightSchedulesList;
    }


    /**
     * 운항 스케줄 조회 (인천공항 제외)
     * @param jsonNode
     * @return
     * @throws JsonProcessingException
     */
    public List<FlightSchedulesDTO> getFlightSchedules(JsonNode jsonNode) throws JsonProcessingException {

        String pSelType = jsonNode.get("pSelType").asText();
        String pFindDate = jsonNode.get("pFindDate").asText();
        String pAirport = jsonNode.get("pAirport").asText();
        String pAirline = jsonNode.get("pAirline").asText();
        String pIotype = jsonNode.get("pIotype").asText();
        String pAgent = jsonNode.get("pAgent").asText();
        String pFightNum = jsonNode.get("pFightNum").asText();

        UriComponentsBuilder flightSchedulesBuilder = UriComponentsBuilder
                .fromUriString("https://www.airport.co.kr/gimhae/ajaxf/frFlightSchSvc/getFilghtSchInternational.do")
                .queryParam("pSelType", pSelType)
                .queryParam("pAirport", pAirport)
                .queryParam("pAirline", pAirline)
                .queryParam("pIotype", pIotype)
                .queryParam("pAgent", pAgent)
                .queryParam("pFightNum", pFightNum);

        String flightSchedulesURL = "";
        if(pSelType.equals("sel")) {
            flightSchedulesURL = flightSchedulesBuilder
                    .queryParam("pFindDate", pFindDate)
                    .build()
                    .toString();
        } else {
            flightSchedulesURL = flightSchedulesBuilder.build().toString();
        }


        String result = restTemplate.getForObject(flightSchedulesURL, String.class);
        JsonNode jsonNodeData = objectMapper.readTree(result).get("data").get("outList");
        List<FlightSchedulesDTO> flightSchedulesList = objectMapper.convertValue(jsonNodeData, new TypeReference<List<FlightSchedulesDTO>>() {});

        return combineScheduleDays(flightSchedulesList);
    }


    /**
     * 인천공항 운항스케줄 조회
     * @param jsonNode
     * @return
     * @throws JsonProcessingException
     */
    public List<FlightSchedulesDTO> getIncheonFlightSchedules(JsonNode jsonNode) throws JsonProcessingException {
        String airportCode = jsonNode.get("pAirline").asText();

        String flightSchedulesURL = UriComponentsBuilder
                .fromUriString("https://www.airport.kr/depReg/ap_ko/getPasRegSchList.do")
                .queryParam("airportCode", airportCode)
                .build()
                .toString();

        String result = restTemplate.getForObject(flightSchedulesURL, String.class);
        JsonNode jsonNodeData = objectMapper.readTree(result).get("scheduleList");
        List<FlightSchedulesDTO> flightSchedulesList = new ArrayList<>();
        jsonNodeData.forEach(json -> {
            FlightSchedulesDTO schedule = FlightSchedulesDTO
                    .builder()
                    .airlineKorean(json.get("airlineNameKo").asText())
                    .airlineEnglish(json.get("airlineNameEn").asText())
                    .airport("인천")
                    .city(json.get("airportKname").asText())
                    .airlineHomepageUrl(json.get("url").asText())
                    .internationalStdt(json.get("gigan").asText())
                    .internationalEddt(json.get("gigan").asText())
                    .internationalNum(json.get("fnumber").asText())
                    .internationalTime(json.get("stime").asText())
                    .internationalMon(json.get("monday").asText())
                    .internationalTue(json.get("tuesday").asText())
                    .internationalWed(json.get("wednesday").asText())
                    .internationalThu(json.get("thursday").asText())
                    .internationalFri(json.get("friday").asText())
                    .internationalSat(json.get("saturday").asText())
                    .internationalSun(json.get("sunday").asText())
                    .internationalIdx(json.get("filePathNm").asText() + json.get("strgFileNm").asText())
                    .build();

            flightSchedulesList.add(schedule);
        });

        return flightSchedulesList;
    }


    /**
     * 운항요일 합치기
     * @param flightSchedulesList
     * @return
     */
    public List<FlightSchedulesDTO> combineScheduleDays(List<FlightSchedulesDTO> flightSchedulesList) {

        for(int i = 0; i < flightSchedulesList.size()-1; i++) {
            FlightSchedulesDTO nowSchedule = flightSchedulesList.get(i);
            FlightSchedulesDTO nextSchedule = flightSchedulesList.get(i+1);

            // 항공사,편명,출발지,도착지,출발시간,도착시간,운항기간 같으면 운항요일 합치기
            if(Objects.equals(nowSchedule.getAirlineKorean(), nextSchedule.getAirlineKorean())
                    && Objects.equals(nowSchedule.getInternationalNum(), nextSchedule.getInternationalNum())
                    && Objects.equals(nowSchedule.getAirport(), nextSchedule.getAirport())
                    && Objects.equals(nowSchedule.getInternationalAirportInte(), nextSchedule.getInternationalAirportInte())
                    && Objects.equals(nowSchedule.getInternationalTime(), nextSchedule.getInternationalTime())
                    && Objects.equals(nowSchedule.getInternationalStdt(), nextSchedule.getInternationalStdt())
                    && Objects.equals(nowSchedule.getInternationalEddt(), nextSchedule.getInternationalEddt())
            ) {
                if(!Objects.equals(nowSchedule.getInternationalMon(), nextSchedule.getInternationalMon())) {
                    nowSchedule.setInternationalMon("Y");
                    nextSchedule.setInternationalMon("Y");
                }
                if(!Objects.equals(nowSchedule.getInternationalTue(), nextSchedule.getInternationalTue())) {
                    nowSchedule.setInternationalTue("Y");
                    nextSchedule.setInternationalTue("Y");
                }
                if(!Objects.equals(nowSchedule.getInternationalWed(), nextSchedule.getInternationalWed())) {
                    nowSchedule.setInternationalWed("Y");
                    nextSchedule.setInternationalWed("Y");
                }
                if(!Objects.equals(nowSchedule.getInternationalThu(), nextSchedule.getInternationalThu())) {
                    nowSchedule.setInternationalThu("Y");
                    nextSchedule.setInternationalThu("Y");
                }
                if(!Objects.equals(nowSchedule.getInternationalFri(), nextSchedule.getInternationalFri())) {
                    nowSchedule.setInternationalFri("Y");
                    nextSchedule.setInternationalFri("Y");
                }
                if(!Objects.equals(nowSchedule.getInternationalSat(), nextSchedule.getInternationalSat())) {
                    nowSchedule.setInternationalSat("Y");
                    nextSchedule.setInternationalSat("Y");
                }
                if(!Objects.equals(nowSchedule.getInternationalSun(), nextSchedule.getInternationalSun())) {
                    nowSchedule.setInternationalSun("Y");
                    nextSchedule.setInternationalSun("Y");
                }
            }

        }

        // List 내 중복된 flightSchedule 제거
        return flightSchedulesList.stream().distinct().toList();
    }
}
