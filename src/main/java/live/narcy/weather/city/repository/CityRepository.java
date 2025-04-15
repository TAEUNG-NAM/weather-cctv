package live.narcy.weather.city.repository;

import live.narcy.weather.city.entity.City;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CityRepository extends JpaRepository<City, Long> {

    List<City> findAllByCountry(String country);

    City findByName(String name);

}
