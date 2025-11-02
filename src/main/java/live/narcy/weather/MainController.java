package live.narcy.weather;

import live.narcy.weather.city.dto.CityDto;
import live.narcy.weather.city.entity.City;
import live.narcy.weather.city.service.CityService;
import live.narcy.weather.views.service.ViewService;
import live.narcy.weather.weatherApi.service.WeatherApiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
        List<CityDto> cities = cityService.getCities("japan");   // 현재 일본만 조회

        model.addAttribute("cities", cities);
        return "contents/home";
    }

    /**
     * 도시 검색
     * @param searchedCity
     * @return
     */
    @GetMapping("/searchCity")
    public String search(@RequestParam("searchedCity") String searchedCity, Model model) {
        Optional<City> optionalCity = cityService.getCityByKeyword(searchedCity);

        return optionalCity
                .map(city -> "redirect:/view/japan?city=" + city.getName()) //  값이 있다면, 리다이렉트 경로를 반환
                .orElseGet(() -> { // 값이 없다면, 에러 페이지 경로를 반환
                    model.addAttribute("searchedCity", searchedCity);
                    return "contents/no-search-result";
                });
    }
}
