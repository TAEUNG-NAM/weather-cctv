package live.narcy.weather.city.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import live.narcy.weather.member.dto.CustomOAuth2User;
import live.narcy.weather.weatherApi.dto.WeatherDetailsDTO;
import live.narcy.weather.city.entity.Area;
import live.narcy.weather.city.entity.City;
import live.narcy.weather.city.service.CityService;
import live.narcy.weather.views.service.ViewService;
import live.narcy.weather.weatherApi.service.WeatherApiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
@RequiredArgsConstructor
@Slf4j
public class MainController {

    private final CityService cityService;
    private final WeatherApiService weatherApiService;
    private final ViewService viewService;

    /**
     * 홈 화면(기본)
     * @param model
     * @return
     */
    @GetMapping("/")
    public String home(Model model) {

        List<City> cities = cityService.getCityList("japan");   // 현재 일본만 조회

        model.addAttribute("cities", cities);

        return "contents/home";
    }

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

        // 모든 도시 조회 -> Select 박스 삽입
        List<City> cities = cityService.getCityList(countryName);
        model.addAttribute("cities", cities);
        model.addAttribute("selectedCity", cityName);

        // 도시명(cityName)을 통해 city 객체 가져온 뒤
        // 가져온 city 객체를 통해 연관된 모든 area객체 가져오기
        City city = cityService.getCity(cityName);
        List<Area> areas = cityService.getAreaList(city);
        model.addAttribute("areas", areas);

//        model.addAttribute("countryName", countryName);

        // 날씨 조회 API
        weatherAPI(cityName, model);

//        String username = customOAuth2User.getUsername();                                         // @AuthenticationPrincipal 어노테이션 방식
        String email = "";
        if (authentication != null && authentication.getPrincipal() instanceof CustomOAuth2User) {  // Authentication을 통해 추출
            email = ((CustomOAuth2User) authentication.getPrincipal()).getEmail();

            // 조회수 증가
            viewService.increaseViewCount(email, city, false);
        } else {
            // 쿠키 검증 및 전처리
            Boolean cookieCheck = checkCookie(request, response, city);

            // 조회수 증가
            viewService.increaseViewCount(email, city, cookieCheck);
        }

        return "contents/cities";
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
//        System.out.println(data);

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
                log.info("Date = {}\t최저 온도 = {}\t최고 온도 = {}", weatherEntry.getKey(), minTemp, maxTemp);

            }

            model.addAttribute("now", weatherDetailsMap.get("now"));
            model.addAttribute("city", cityName);
            model.addAttribute("weatherMap", weatherDetailsMap);
            model.addAttribute("minMaxTempMap", minMaxTempMap);
        }

    }

    /**
     * 도시 검색 기능
     * @param searchedCity
     * @return
     */
    @GetMapping("/searchCity")
    public String search(@RequestParam("searchedCity") String searchedCity, Model model) {
        log.info("searchedCity = {}", searchedCity);

        City cityByEng = cityService.getCity(searchedCity);
        if(cityByEng != null) {
            return "redirect:/view/japan?city=" + searchedCity;
        }

        City cityByKor = cityService.getCityByKor(searchedCity);
        if(cityByKor != null) {
            return "redirect:/view/japan?city=" + cityByKor.getName();
        }

        // 조회 실패 시 에러페이지로 리다이렉트
        model.addAttribute("searchedCity", searchedCity);
        return "contents/no-search-result";
    }

}
