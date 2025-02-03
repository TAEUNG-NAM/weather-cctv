package live.narcy.weather.controller;

import live.narcy.weather.entity.Area;
import live.narcy.weather.entity.City;
import live.narcy.weather.repository.AreaRepository;
import live.narcy.weather.repository.CityRepository;
import live.narcy.weather.service.CityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class MainController {

    private final CityService cityService;

    @GetMapping("/home")
    public String home(Model model) {

        List<City> cities = cityService.getCityList("japan");

        for(City city : cities) {
            log.info("국가={}, 도시={}, api={}", city.getCountry(), city.getName(), city.getWeatherApi());
        }

        model.addAttribute("cities", cities);

        return "contents/home";
    }

    @GetMapping("/{country}")
    public String viewCity(@PathVariable("country") String countryName, @RequestParam("city") String cityName, Model model) {

        // 모든 도시 조회 -> Select 박스 삽입
        List<City> cities = cityService.getCityList(countryName);
        model.addAttribute("cities", cities);
        model.addAttribute("selectedCity", cityName);

        // 도시명(cityName)을 통해 city 객체 가져온 뒤
        // 가져온 city 객체를 통해 연관된 모든 area객체 가져오기
        City city = cityService.getCity(cityName);
        List<Area> areas = cityService.getAreaList(city);
        model.addAttribute("areas", areas);

        for (Area area : areas) {
            System.out.println("areaByCity = " + area.getName());
        }

//        List<Area> areas = areaRepository.findAll();
//        for (Area area : areas) {
//            System.out.println("area = " + area.getName());
//            System.out.println("area.getCity = " + area.getCity().getName());
//        }

        log.info("국가명 = {}", countryName);
        log.info("도시명 = {}", cityName);

        return "contents/cities";
    }


    @GetMapping("/lay")
    public String layout() {
        return "contents/main";
    }

}
