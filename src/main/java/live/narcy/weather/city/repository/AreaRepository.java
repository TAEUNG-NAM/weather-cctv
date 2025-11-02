package live.narcy.weather.city.repository;

import live.narcy.weather.city.entity.Area;
import live.narcy.weather.city.entity.City;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AreaRepository extends JpaRepository<Area, Long>, AreaRepositoryCustom {
    List<Area> findByCity(City city);
}
