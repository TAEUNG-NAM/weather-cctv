package live.narcy.weather.city.repository;

import live.narcy.weather.city.entity.City;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CityRepository extends JpaRepository<City, Long>, CityRepositoryCustom{

    Optional<City> findByName(String name);

    Optional<City> findByKorName(String korName);

    boolean existsByName(String engName);

}
