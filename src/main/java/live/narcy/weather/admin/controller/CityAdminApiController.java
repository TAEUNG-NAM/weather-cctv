package live.narcy.weather.admin.controller;

import live.narcy.weather.city.dto.CityDto;
import live.narcy.weather.city.entity.City;
import live.narcy.weather.city.service.CityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@RequiredArgsConstructor
@Controller
@Slf4j
@RequestMapping("/api/admin/")
public class CityAdminApiController {

    private final CityService cityService;
//    private final CountryRepository countryRepository;    // 추후 생성 필요!


    /**
     * 특정 국가의 모든 도시 조회
     * @param countryName
     * @return
     */
    @GetMapping("management/city")
    public ResponseEntity<?> getCity(@RequestParam(name = "country") String countryName) {
        // Country에 해당하는 City조회
        List<CityDto> cities = cityService.getAllCities(countryName);

        return ResponseEntity.ok(cities);
    }

    /**
     * 도시(City) 추가
     * @param param
     * @return
     */
    @PostMapping("management/city")
    public ResponseEntity<?> addCity(@ModelAttribute CityDto param,
                                     @RequestParam(name = "image") MultipartFile thumbnail) {

        CityDto savedCity = cityService.saveCity(param, thumbnail);

        return ResponseEntity.ok(savedCity);
    }

    /**
     * 도시(City) 수정
     * @param param
     * @param thumbnail
     * @return
     */
    @PatchMapping("management/city")
    public ResponseEntity<?> updateCity(@ModelAttribute CityDto param,
                                        @RequestParam(name = "image", required = false) MultipartFile thumbnail) {

        log.info("도시수정 요청 param = {}", param);

        CityDto updatedCity = cityService.updateCity(param, thumbnail);

        return ResponseEntity.ok(updatedCity);
    }

    /**
     * 도시(City) 삭제
     * @param param
     * @return
     */
    @DeleteMapping("management/city")
    public ResponseEntity<?> deleteCity(@ModelAttribute CityDto param) {
        String targetCountry = param.getCountry();
        Long targetCityId = param.getId();
        log.info("country = {}", targetCountry);
        log.info("cityId = {}", targetCityId);

        cityService.deleteCity(targetCountry, targetCityId);

        return ResponseEntity.ok(Map.of("message", "삭제 완료"));
    }
}
