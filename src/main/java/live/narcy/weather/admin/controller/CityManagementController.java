package live.narcy.weather.admin.controller;

import live.narcy.weather.city.dto.*;
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
import java.util.*;

@RequiredArgsConstructor
@Controller
@Slf4j
public class CityManagementController {

    private final ViewService viewService;
    private final CityService cityService;
    private final AreaService areaService;
//    private final CountryRepository countryRepository;    // 추후 생성 필요!

    @GetMapping("/admin/management")
    public String adminCityManagePage(Model model) {

        return "contents/cityManagement";
    }


    /**
     * 도시(City) 추가
     * @param param
     * @param thumbnail
     * @return
     */
    @PostMapping("/api/admin/management/city")
    public ResponseEntity<?> addCity(@RequestParam Map<String, String> param,
                                     @RequestParam("image") MultipartFile thumbnail) {

        City savedCity = cityService.saveCity(param, thumbnail);

        return (savedCity != null) ?
                ResponseEntity.ok().build() :
                ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    /**
     * 도시(City) 수정
     * @param param
     * @param thumbnail
     * @return
     */
    @PatchMapping("/api/admin/management/city")
    public ResponseEntity<?> updateCity(@RequestParam Map<String, String> param,
                                        @RequestParam(name = "image", required = false) MultipartFile thumbnail) {

        log.info("도시수정 요청 param = {}", param);

        City updatedCity = cityService.updateCity(param, thumbnail);

        return (updatedCity != null) ?
                ResponseEntity.ok().build() :
                ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

    }


    /**
     * 도시(City) 삭제
     * @param param
     * @return
     */
    @DeleteMapping("/api/admin/management/city")
    public ResponseEntity<?> deleteCity(@RequestBody Map<String,String> param) {
        String targetCountry = param.getOrDefault("country", "");
        String targetCityId = param.getOrDefault("cityId", "");
        log.info("country = {}", targetCountry);
        log.info("cityId = {}", targetCityId);

        City deletedCity = cityService.deleteCity(targetCountry, targetCityId);

        return (deletedCity != null) ?
                ResponseEntity.ok().build() :
                ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }
}
