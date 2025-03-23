package live.narcy.weather.repository;

import live.narcy.weather.entity.City;
import live.narcy.weather.entity.Views;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface ViewRepository extends JpaRepository<Views, Long> {

    boolean existsByMemberIdAndCityIdAndViewedAtAfter(Long memberId, Long cityId, LocalDateTime afterTime);

    int countByCityIdAndViewedAt(Long cityId, LocalDateTime year);

}
