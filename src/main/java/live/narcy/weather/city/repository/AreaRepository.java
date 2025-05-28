package live.narcy.weather.city.repository;

import live.narcy.weather.city.entity.Area;
import live.narcy.weather.city.entity.City;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AreaRepository extends JpaRepository<Area, Long> {

    List<Area> findByCityAndDelYn(City city, String delYn);
    List<Area> findByCity(City city);
}
