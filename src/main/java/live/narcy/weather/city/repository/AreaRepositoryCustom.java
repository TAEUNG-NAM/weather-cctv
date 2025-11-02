package live.narcy.weather.city.repository;

import live.narcy.weather.city.entity.Area;

import java.util.List;

public interface AreaRepositoryCustom {
    List<Area> findByCityName(String cityName);
}
