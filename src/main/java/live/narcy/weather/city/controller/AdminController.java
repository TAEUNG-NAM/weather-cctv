package live.narcy.weather.city.controller;

import live.narcy.weather.city.dto.*;
import live.narcy.weather.city.entity.Area;
import live.narcy.weather.city.entity.City;
import live.narcy.weather.city.service.AreaService;
import live.narcy.weather.city.service.CityService;
import live.narcy.weather.city.service.ViewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;

@RequiredArgsConstructor
@Controller
@RequestMapping("/admin")
public class AdminController {

    private final ViewService viewService;
    private final CityService cityService;
    private final AreaService areaService;
//    private final CountryRepository countryRepository;    // 추후 생성 필요!

    @GetMapping("/city/chart")
    public String adminChartPage(Model model) {

        LocalDate now = LocalDate.now();
        int thisYear = now.getYear();
        List<Integer> years = new ArrayList<>();

        for(int i = thisYear; i > thisYear-10; i--) {
            years.add(i);
        }

        model.addAttribute("years", years);
        return "contents/admin";
    }

    @GetMapping("/city/manage")
    public String adminCityManagePage(Model model) {

        return "contents/admin2";
    }

    /**
     * 선택된 (Year, Country, City)의 조회수 조회(AreaChart)
     * @param year
     * @param country
     * @param city
     * @return
     */
    @ResponseBody
    @GetMapping("/api/city/area-chart")
    public ResponseEntity<?> getCityViewsData(@RequestParam(name = "year") String year,
                                              @RequestParam(name = "country") String country,
                                              @RequestParam(name = "city") String city) {

        System.out.println("year = " + year);
        System.out.println("country = " + country);
        System.out.println("city = " + city);

        Map<String, Object> cityData = new HashMap<>();

        List<String> labels = new ArrayList<>();
        List<Long> values = new ArrayList<>();

        List<MonthlyViewCountInterface> viewCountList = viewService.getViewsCount(year, country, city);

        for(MonthlyViewCountInterface c : viewCountList) {
            labels.add(c.getMonth() + "월");
            values.add(c.getViewCount());
        }

        cityData.put("labels", labels);
        cityData.put("data", values);

        return ResponseEntity.ok(cityData);
    }

    /**
     * 선택된 (Year, Country)의 조회수 조회(DonutChart)
     * @param year
     * @param country
     * @return
     */
    @ResponseBody
    @GetMapping("/api/city/donut-chart")
    public ResponseEntity<?> getCityViewsDonutData(@RequestParam(name = "year") String year,
                                              @RequestParam(name = "country") String country) {

        System.out.println("year = " + year);
        System.out.println("country = " + country);

        Map<String, Object> cityData = new HashMap<>();

        List<String> labels = new ArrayList<>();
        List<Long> values = new ArrayList<>();

        List<YearlyCountryViewRatioInterface> yearlyCountryViewList = viewService.getViewsRatio(year, country);

        for(YearlyCountryViewRatioInterface ratio : yearlyCountryViewList) {
            labels.add(ratio.getCity());
            values.add(ratio.getViewCount());
            System.out.println("city: " + ratio.getCity());
            System.out.println("views: " + ratio.getViewCount());
        }

        cityData.put("labels", labels);
        cityData.put("data", values);

        return ResponseEntity.ok(cityData);
    }

    /**
     * 조회수차트 City 셀렉트 목록 조회
     * @return
     */
    @GetMapping("/api/city/list")
    public ResponseEntity<?> getCityList(@RequestParam(name = "country", required = false) String countryName) {

        // Country에 해당하는 city 조회
        List<City> cities = cityService.getCityList(countryName);

        return cities != null ?
                ResponseEntity.ok(cities) :
                ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    /**
     * Area-CCTV 정보 조회
     * @return ResponseEntity
     */
    @GetMapping("/api/get-cctv-data")
    public ResponseEntity<?> getCctvData(@RequestParam(name = "city", required = false) String cityName) {

        System.out.println("cityName: " + cityName);

        List<Area> cctvData = new ArrayList<>();
        if(cityName != null && !"0".equals(cityName)) {
            cctvData = areaService.getCctvData(cityName);
        }

        return cctvData != null ?
                ResponseEntity.ok(cctvData):
                ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    /**
     * Area-CCTV 정보 수정
     * @param dtoList
     * @return ResponseEntity
     */
    @PatchMapping("/api/update-cctv-data")
    public ResponseEntity<?> updateCctvData(@RequestBody List<AreaDTO> dtoList) {

        for(AreaDTO area : dtoList) {
            System.out.println(area.getName() + ":" + area.getId() + ":" + area.getCctvSrc() + ":" + area.getMapSrc());
        }

        List<AreaDTO> updatedList = areaService.updateCctvData(dtoList);

        return updatedList != null && dtoList.size() == updatedList.size() ?
                ResponseEntity.ok(updatedList) :
                ResponseEntity.status(HttpStatus.BAD_REQUEST).body(updatedList);
    }

}
