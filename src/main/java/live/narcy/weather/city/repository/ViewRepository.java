package live.narcy.weather.city.repository;

import live.narcy.weather.city.dto.MonthlyViewCount;
import live.narcy.weather.city.dto.MonthlyViewCountInterface;
import live.narcy.weather.city.dto.YearlyCountryViewRatio;
import live.narcy.weather.city.dto.YearlyCountryViewRatioInterface;
import live.narcy.weather.city.entity.Views;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Repository
public interface ViewRepository extends JpaRepository<Views, Long> {

    boolean existsByMemberIdAndCityIdAndViewedAtAfter(Long memberId, Long cityId, LocalDateTime afterTime);

    int countByCityIdAndViewedAtBetween(Long cityId, LocalDateTime start, LocalDateTime end);

    @Query(value = "SELECT MONTH(viewedAt) AS month, COUNT(*) AS viewCount " +
            "FROM views " +
            "WHERE YEAR(viewedAt) = :year AND city_id = :cityId " +
            "GROUP BY month " +
            "ORDER BY month", nativeQuery = true)
    List<MonthlyViewCountInterface> countMonthlyCityViewCounts(@Param("cityId") Long cityId, @Param("year") int year);


    @Query(value = "SELECT MONTH(viewedAt) AS month, COUNT(*) AS viewCount " +
            "FROM views v " +
            "JOIN city c ON v.city_id = c.id " +
            "WHERE YEAR(viewedAt) = :year AND c.country = :country " +
            "GROUP BY month " +
            "ORDER BY month", nativeQuery = true)
    List<MonthlyViewCountInterface> countMonthlyCountryViewCounts(@Param("country") String country, @Param("year") int year);

    @Query(value = "SELECT c.name AS city, COUNT(*) AS viewCount " +
            "FROM views v " +
            "JOIN city c ON v.city_id = c.id " +
            "WHERE YEAR(viewedAt) = :year AND c.country = :country " +
            "GROUP BY c.name " +
            "ORDER BY viewCount DESC", nativeQuery = true)
    List<YearlyCountryViewRatioInterface> countYearlyCountryViewRatio(@Param("country") String country, @Param("year") int year);
}
