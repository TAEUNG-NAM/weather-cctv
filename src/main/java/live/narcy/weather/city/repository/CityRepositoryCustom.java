package live.narcy.weather.city.repository;

import live.narcy.weather.city.dto.CityDto;

import java.util.List;

public interface CityRepositoryCustom {
    // 숨겨진(delYn=y) 도시 제외
    List<CityDto> findCitiesByCountryName(String name);
    // 숨겨진(delYn=y) 도시 포함
    List<CityDto> findAllCitiesByCountryName(String name);
}
