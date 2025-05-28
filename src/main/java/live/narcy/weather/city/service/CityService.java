package live.narcy.weather.city.service;

import jakarta.persistence.EntityNotFoundException;
import live.narcy.weather.city.entity.Area;
import live.narcy.weather.city.entity.City;
import live.narcy.weather.city.repository.AreaRepository;
import live.narcy.weather.city.repository.CityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CityService {

    private final CityRepository cityRepository;
    private final AreaRepository areaRepository;

    public List<City> getCityList(String countryName) {
        return cityRepository.findByCountryAndDelYn(countryName, "n");
    }

    public List<City> getCityAllList(String countryName) {
        return cityRepository.findAllByCountry(countryName);
    }

    public City getCity(String cityName) {
        return cityRepository.findByName(cityName);
    }

    public City getCityByKor(String cityName) {
        return cityRepository.findByKorName(cityName);
    }

    public List<Area> getAreaList(City city) {
        return areaRepository.findByCityAndDelYn(city, "n");
    }


    @Transactional
    public City saveCity(Map<String, String> param, MultipartFile thumbnail) {
        String ogFileName = thumbnail.getOriginalFilename();
        int lastIndexOfDot = Objects.requireNonNull(ogFileName).lastIndexOf(".");
        String extension = ogFileName.substring(lastIndexOfDot);

        try {
            thumbnail.transferTo(new File("C:/Users/atrix/OneDrive/바탕 화면/Git/Narcy/src/main/resources/static/assets/img/gallery/" + param.get("cityName") + extension));

            City newCity = City.builder()
                    .name(param.get("cityName"))
                    .country(param.get("country"))
                    .thumbnail("/assets/img/gallery/" + param.get("cityName") + extension)
                    .delYn("n")
                    .build();

            return cityRepository.save(newCity);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Transactional
    public City updateCity(Map<String, String> param, MultipartFile thumbnail) {
        City targetCity = cityRepository.findById(Long.valueOf(param.get("cityId"))).orElseThrow(
                () -> new EntityNotFoundException("해당 도시를 찾을 수 없습니다.")
        );

        City newCity = City.builder()
                .id(Long.valueOf(param.get("cityId")))
                .country(param.get("country"))
                .name(param.get("cityName"))
                .delYn(param.get("delYn"))
                .build();

        City updatedCity = targetCity.patch(newCity);

        cityRepository.save(updatedCity);

        return updatedCity;
    }

    @Transactional
    public City deleteCity(String targetCountry, String targetCityId) {
        City targetCity = cityRepository.findById(Long.valueOf(targetCityId)).orElseThrow(
                () -> new EntityNotFoundException("해당 도시를 찾을 수 없습니다.")
        );

        cityRepository.deleteById(Long.valueOf(targetCityId));

        return targetCity;
    }
}
