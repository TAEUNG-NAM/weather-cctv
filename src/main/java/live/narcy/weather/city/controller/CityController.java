package live.narcy.weather.city.controller;

import io.jsonwebtoken.lang.Strings;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import live.narcy.weather.city.dto.AreaDto;
import live.narcy.weather.city.dto.CityDto;
import live.narcy.weather.city.entity.City;
import live.narcy.weather.city.service.AreaService;
import live.narcy.weather.city.service.CityService;
import live.narcy.weather.member.dto.CustomOAuth2User;
import live.narcy.weather.views.service.ViewService;
import live.narcy.weather.weatherApi.dto.WeatherDetailsDTO;
import live.narcy.weather.weatherApi.service.WeatherApiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

@Controller
@RequiredArgsConstructor
@Slf4j
public class CityController {

    private final ViewService viewService;
    private final CityService cityService;
    private final AreaService areaService;
    private final WeatherApiService weatherApiService;

    /**
     * 특정 도시 CCTV 및 날씨(API) 조회
     * @param countryName
     * @param cityName
     * @param model
     * @return
     */
    @GetMapping("/view/{country}")
    public String viewCity(@PathVariable("country") String countryName,
                           @RequestParam("city") String cityName,
                           Model model,
                           Authentication authentication,
                           @AuthenticationPrincipal CustomOAuth2User customOAuth2User,
                           HttpServletRequest request,
                           HttpServletResponse response) {

        // 도시 검증 및 조회
        City city = cityService.getCity(cityName);

        // 모든 도시 조회 -> Select 박스 삽입
        List<CityDto> cities = cityService.getCities(countryName);
        model.addAttribute("cities", cities);
        model.addAttribute("selectedCity", cityName);

        // 조회할 도시의 모든 구역(cctv) 가져오기
        List<AreaDto> areas = areaService.getAreaList(cityName);
        model.addAttribute("areas", areas);

        // 날씨 조회 API
        weatherAPI(cityName, model);

//        String username = customOAuth2User.getUsername();                                         // @AuthenticationPrincipal 어노테이션 방식
        String email = "";
        if (authentication != null && authentication.getPrincipal() instanceof CustomOAuth2User) {  // Authentication을 통해 추출
            email = ((CustomOAuth2User) authentication.getPrincipal()).getEmail();

            // 조회수 증가(회원)
            viewService.recordViewHistory(email, city);
        } else {
            // 쿠키 검증 및 전처리
            Boolean cookieCheck = checkCookie(request, response, city);
            if (!cookieCheck && !Strings.hasText(email)) {
                // 조회수 증가(비회원)
                viewService.recordViewHistoryAnonymous(city);
            }
        }

        return "contents/cities";
    }

    /**
     * 올해의 도시 페이지
     * @param model
     * @return
     */
    @GetMapping("/best-city")
    public String bestCityPage(Model model) {

        Map<String, String> topCityMap =  viewService.getTopCityViews();

        model.addAttribute("topCityMap", topCityMap);

        return "contents/best-city";
    }

    /**
     * 비회원 쿠키 검증 및 전처리
     * @param request
     * @param response
     * @param city
     * @return
     */
    private Boolean checkCookie(HttpServletRequest request, HttpServletResponse response, City city) {
        Cookie[] cookies = request.getCookies();
        String cookieName = "viewedCity_" + city.getId();
        boolean isViewed = false;

        if(cookies != null) {
            for(Cookie cookie : cookies) {
                if(cookie.getName().equals(cookieName)) {
                    isViewed = true;
                    break;
                }
            }
        }

        // 조회 플래그(쿠키) 없을 경우
        if(!isViewed) {
            Cookie newCookie = new Cookie(cookieName, "true");
            newCookie.setMaxAge(60 * 5);    // 5분
            newCookie.setPath("/");         // 전체 경로에 적용
            response.addCookie(newCookie);
            return isViewed;
        }
        return isViewed;
    }

    /**
     * 날씨 조회 API 호출
     * @param cityName
     * @param model
     */
    @ResponseBody
    private void weatherAPI(String cityName, Model model) {
        ResponseEntity<Map<String, List<WeatherDetailsDTO>>> weatherResponseEntity = weatherApiService.getWeather(cityName);
        if(weatherResponseEntity.getStatusCode().is2xxSuccessful()) {

            Map<String, List<WeatherDetailsDTO>> weatherDetailsMap = weatherResponseEntity.getBody();
            Map<String, Map<String, Double>> minMaxTempMap = new HashMap<>();

            // 날씨 정보
            for (Map.Entry<String, List<WeatherDetailsDTO>> weatherEntry : weatherDetailsMap.entrySet()) {

                // 최저 온도 구하기
                Double minTemp2 = weatherDetailsMap.get(weatherEntry.getKey())
                        .stream()
                        .map(WeatherDetailsDTO::getTemp)
                        .min(Double::compareTo)
                        .get();

                // 최고 온도 구하기
                Double maxTemp2 = weatherDetailsMap.get(weatherEntry.getKey())
                        .stream()
                        .map(WeatherDetailsDTO::getTemp)
                        .max(Double::compareTo)
                        .get();


                // 스트림을 통한 최대/최소값 구하기
                List<WeatherDetailsDTO> weatherDetails = weatherDetailsMap.get(weatherEntry.getKey());

                DoubleSummaryStatistics stats = weatherDetails.stream()
                        .mapToDouble(WeatherDetailsDTO::getTemp)
                        .summaryStatistics();

                // 최저, 최고 온도 구하기
                double maxTemp = stats.getMax();
                double minTemp = stats.getMin();

                // 익명 클래스를 사용한 초기화
//                minMaxTempMap.put(weatherEntry.getKey(), new HashMap<String, Double>() {{
//                   put("max", maxTemp);
//                   put("min", minTemp);
//                }});

                // Map.of를 사용한 초기화(Java9이후)
                minMaxTempMap.put(weatherEntry.getKey(), Map.of(
                        "max", maxTemp,
                        "min", minTemp
                ));

//                log.info("Date = {}", weatherEntry.getKey());
//                log.info("Date = {}\t최저 온도 = {}\t최고 온도 = {}", weatherEntry.getKey(), minTemp, maxTemp);

            }

            model.addAttribute("now", weatherDetailsMap.get("now"));
            model.addAttribute("city", cityName);
            model.addAttribute("weatherMap", weatherDetailsMap);
            model.addAttribute("minMaxTempMap", minMaxTempMap);
        }

    }
}
