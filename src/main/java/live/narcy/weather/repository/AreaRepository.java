package live.narcy.weather.repository;

import live.narcy.weather.entity.Area;
import live.narcy.weather.entity.City;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AreaRepository extends JpaRepository<Area, Long> {

    List<Area> findByCity(City city);
}
