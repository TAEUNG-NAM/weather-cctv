package live.narcy.weather.views.repository;

import live.narcy.weather.views.dto.MonthlyViewCountInterface;
import live.narcy.weather.views.dto.YearlyCountryViewRatio;
import live.narcy.weather.views.dto.YearlyCountryViewRatioInterface;
import live.narcy.weather.views.entity.Views;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ViewRepository extends JpaRepository<Views, Long> {

    boolean existsByMemberIdAndCityIdAndViewedAtAfter(Long memberId, Long cityId, LocalDateTime afterTime);

    @Query(value = "SELECT CONCAT(c.korName, '-', c.name) AS city, COUNT(*) AS viewCount " +
            "FROM views v " +
            "JOIN city c ON v.city_id = c.id " +
            "WHERE YEAR(v.viewedAt) = YEAR(NOW()) " +
            "AND c.del_yn = 'n'" +
            "GROUP BY v.city_id " +
            "ORDER BY viewCount DESC " +
            "limit 5", nativeQuery = true)
    List<YearlyCountryViewRatioInterface> countCityTopViewCounts();

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
