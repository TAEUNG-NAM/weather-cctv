package live.narcy.weather.admin.controller;

import live.narcy.weather.city.dto.AreaDto;
import live.narcy.weather.city.dto.CityDto;
import live.narcy.weather.city.service.AreaService;
import live.narcy.weather.city.service.CityService;
import live.narcy.weather.views.dto.MonthlyViewCount;
import live.narcy.weather.views.dto.YearlyCountryViewRatio;
import live.narcy.weather.views.service.ViewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@Slf4j
@RequestMapping("/api/")
public class AreaAdminApiController {

    private final ViewService viewService;
    private final CityService cityService;
    private final AreaService areaService;
//    private final CountryRepository countryRepository;    // 추후 생성 필요!

    /**
     * 선택된 (Year, Country, City)의 조회수 조회(AreaChart)
     * @param year
     * @param country
     * @param city
     * @return
     */
    @GetMapping("admin/area-chart/views")
    public ResponseEntity<?> getCityViewsData(@RequestParam(name = "year") String year,
                                              @RequestParam(name = "country") String country,
                                              @RequestParam(name = "city") String city) {

        log.info("year = {}, country = {}, city = {}", year, country, city);

        Map<String, Object> cityData = new HashMap<>();

        List<String> labels = new ArrayList<>();
        List<Long> values = new ArrayList<>();

        List<MonthlyViewCount> viewCountList = viewService.getViewsCount(year, country, city);

        for(MonthlyViewCount c : viewCountList) {
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
    @GetMapping("admin/donut-chart/views")
    public ResponseEntity<?> getCityViewsDonutData(@RequestParam(name = "year") String year,
                                              @RequestParam(name = "country") String country) {

        log.info("year = {}, country = {}", year, country);

        Map<String, Object> cityData = new HashMap<>();

        List<String> labels = new ArrayList<>();
        List<Long> values = new ArrayList<>();

        List<YearlyCountryViewRatio> yearlyCountryViewList = viewService.getViewsRatio(year, country);

        for(YearlyCountryViewRatio ratio : yearlyCountryViewList) {
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
    @GetMapping("admin/area-chart/city-list")
    public ResponseEntity<?> getCityList(@RequestParam(name = "country", required = false) String countryName) {

        // Country에 해당하는 City 조회
        List<CityDto> cities = cityService.getAllCities(countryName);

        return ResponseEntity.ok(cities);
    }

    /**
     * 도시별 Area-CCTV 정보 조회
     * @return ResponseEntity
     */
    @GetMapping("admin/cctv-data")
    public ResponseEntity<?> getCctvData(@RequestParam(name = "city", required = false) String cityName) {
        List<AreaDto> cctvData = new ArrayList<>();
        if(cityName != null && !"0".equals(cityName)) {
            cctvData = areaService.getCctvData(cityName);
        }

        for(AreaDto dto : cctvData) {
            log.info("Area = {}", dto);
        }

        return ResponseEntity.ok(cctvData);
    }

    /**
     * Area-CCTV 정보 수정&생성
     * @param dtoList
     * @return ResponseEntity
     */
    @PostMapping("admin/cctv-data")
    public ResponseEntity<?> updateCctvData(@RequestBody List<AreaDto> dtoList) {
        for(AreaDto area : dtoList) {
            log.info("area = {}", area);
        }
        areaService.updateCctvData(dtoList);
        return ResponseEntity.ok(Map.of("message", "수정 완료"));
    }

    /**
     * Area-CCTV 정보 삭제
     * @param cctvId
     * @return ResponseEntity
     */
    @DeleteMapping("admin/cctv-data")
    public ResponseEntity<?> deleteCctvData(@RequestParam(name = "cctvId") Long cctvId) {

        log.info("cctvID = {}", cctvId);
        areaService.deleteCctvData(cctvId);
        return ResponseEntity.ok(Map.of("message", "삭제 완료"));
    }
}
