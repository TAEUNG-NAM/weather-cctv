package live.narcy.weather.admin.controller;

import live.narcy.weather.city.dto.AreaDTO;
import live.narcy.weather.city.entity.Area;
import live.narcy.weather.city.entity.City;
import live.narcy.weather.city.service.AreaService;
import live.narcy.weather.city.service.CityService;
import live.narcy.weather.views.dto.MonthlyViewCountInterface;
import live.narcy.weather.views.dto.YearlyCountryViewRatioInterface;
import live.narcy.weather.views.service.ViewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Controller
@Slf4j
public class AreaManagementController {

    private final ViewService viewService;
    private final CityService cityService;
    private final AreaService areaService;
//    private final CountryRepository countryRepository;    // 추후 생성 필요!

    @GetMapping("/admin/chart")
    public String adminChartPage(Model model) {

        LocalDate now = LocalDate.now();
        int thisYear = now.getYear();
        List<Integer> years = new ArrayList<>();

        for(int i = thisYear; i > thisYear-10; i--) {
            years.add(i);
        }

        model.addAttribute("years", years);
        return "contents/areaManagement";
    }


    /**
     * 선택된 (Year, Country, City)의 조회수 조회(AreaChart)
     * @param year
     * @param country
     * @param city
     * @return
     */
    @ResponseBody
    @GetMapping("/api/admin/area-chart/views")
    public ResponseEntity<?> getCityViewsData(@RequestParam(name = "year") String year,
                                              @RequestParam(name = "country") String country,
                                              @RequestParam(name = "city") String city) {

        log.info("year = {}, country = {}, city = {}", year, country, city);

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
    @GetMapping("/api/admin/donut-chart/views")
    public ResponseEntity<?> getCityViewsDonutData(@RequestParam(name = "year") String year,
                                              @RequestParam(name = "country") String country) {

        log.info("year = {}, country = {}", year, country);

        Map<String, Object> cityData = new HashMap<>();

        List<String> labels = new ArrayList<>();
        List<Long> values = new ArrayList<>();

        List<YearlyCountryViewRatioInterface> yearlyCountryViewList = viewService.getViewsRatio(year, country);

        for(YearlyCountryViewRatioInterface ratio : yearlyCountryViewList) {
            labels.add(ratio.getCity());
            values.add(ratio.getViewCount());
            log.info("city = {}, views = {}", ratio.getCity(), ratio.getViewCount());
        }

        cityData.put("labels", labels);
        cityData.put("data", values);

        return ResponseEntity.ok(cityData);
    }

    /**
     * AreaChart 셀렉트(City) 목록 조회
     * @return
     */
    @GetMapping("/api/admin/area-chart/city-list")
    public ResponseEntity<?> getCityList(@RequestParam(name = "country", required = false) String countryName) {

        // Country에 해당하는 city 조회
        List<City> cities = cityService.getCityAllList(countryName);

        return cities != null ?
                ResponseEntity.ok(cities) :
                ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    /**
     * 도시별 Area-CCTV 정보 조회
     * @return ResponseEntity
     */
    @GetMapping("/api/admin/cctv-data")
    public ResponseEntity<?> getCctvData(@RequestParam(name = "city", required = false) String cityName) {
        List<Area> cctvData = new ArrayList<>();
        if(cityName != null && !"0".equals(cityName)) {
            cctvData = areaService.getCctvData(cityName);
        }

        for(Area a : cctvData) {
            log.info("Area = {}", a);
        }

        return !cctvData.isEmpty() ?
                ResponseEntity.ok(cctvData):
                ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    /**
     * Area-CCTV 정보 수정
     * @param dtoList
     * @return ResponseEntity
     */
    @PostMapping("/api/admin/cctv-data")
    public ResponseEntity<?> updateCctvData(@RequestBody List<AreaDTO> dtoList) {

        for(AreaDTO area : dtoList) {
            log.info("area = {}", area);
        }

        List<AreaDTO> updatedList = areaService.updateCctvData(dtoList);

        return updatedList != null && dtoList.size() == updatedList.size() ?
                ResponseEntity.ok(updatedList) :
                ResponseEntity.status(HttpStatus.BAD_REQUEST).body(updatedList);
    }

    /**
     * Area-CCTV 정보 삭제
     * @param cctvId
     * @return ResponseEntity
     */
    @DeleteMapping("/api/admin/cctv-data")
    public ResponseEntity<?> deleteCctvData(@RequestParam(name = "cctvId") Long cctvId) {

        log.info("cctvID = {}", cctvId);
        areaService.deleteCctvData(cctvId);
        return ResponseEntity.ok().build();
    }

}
