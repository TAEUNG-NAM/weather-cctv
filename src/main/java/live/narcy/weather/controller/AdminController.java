package live.narcy.weather.controller;

import live.narcy.weather.entity.City;
import live.narcy.weather.repository.CityRepository;
import live.narcy.weather.repository.ViewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

@RequiredArgsConstructor
@Controller
public class AdminController {

    private final ViewRepository viewRepository;
    private final CityRepository cityRepository;
//    private final CountryRepository countryRepository;    // 추후 생성 필요!

    @GetMapping("/city/admin")
    public String adminPage() {

        return "contents/admin";
    }

    /**
     * 선택된 (Year, Country, City)의 조회수 조회
     * @param year
     * @param country
     * @param city
     * @return
     */
    @ResponseBody
    @GetMapping("/api/city/chart")
    public ResponseEntity<?> getCityViewsData(@RequestParam(name = "year") String year,
                                              @RequestParam(name = "country") String country,
                                              @RequestParam(name = "city") String city) {

        System.out.println("year = " + year);
        System.out.println("country = " + country);
        System.out.println("city = " + city);

//        viewRepository.existsByMemberIdAndCityIdAndViewedAtAfter();

        Map<String, Object> cityData = new HashMap<>();

        List<String> labels = new ArrayList<>();
        List<Integer> data = new ArrayList<>();
        for(int i = 1; i <= 12; i++) {
            labels.add(i+"월");
            data.add((int) ((Math.random()*500)+1));
        }

        cityData.put("labels", labels);
        cityData.put("data", data);

        return ResponseEntity.ok(cityData);
    }

    /**
     * City 목록
     * @return
     */
    @GetMapping("/api/city/list")
    public ResponseEntity<?> getCityList(@RequestParam(name = "country", required = false) Long countryId) {

        /* Country 테이블 생성 후 선택된 CountryId 에 해당하는 City 불러와야함 */
        List<City> cities = cityRepository.findAll();
        List<String> cityNames = new ArrayList<>();

        for(City c : cities) {
            String cityName = c.getName();
            cityNames.add(cityName);
        }

        Map<String, List<String>> cityList = new HashMap<>();
        cityList.put("cities", cityNames);

        return ResponseEntity.ok(cityList);
    }

}
