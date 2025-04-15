package live.narcy.weather.city.service;

import live.narcy.weather.city.entity.Area;
import live.narcy.weather.city.entity.City;
import live.narcy.weather.city.repository.AreaRepository;
import live.narcy.weather.city.repository.CityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CityService {

    private final CityRepository cityRepository;
    private final AreaRepository areaRepository;

    public List<City> getCityList(String countryName) {
        return cityRepository.findAllByCountry(countryName);
    }

    public City getCity(String cityName) {
        return cityRepository.findByName(cityName);
    }

    public List<Area> getAreaList(City city) {
        return areaRepository.findByCity(city);
    }
}
