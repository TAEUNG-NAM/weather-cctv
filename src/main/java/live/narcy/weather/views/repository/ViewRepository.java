package live.narcy.weather.views.repository;

import live.narcy.weather.views.entity.Views;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface ViewRepository extends JpaRepository<Views, Long>, ViewRepositoryCustom {

    boolean existsByMemberIdAndCityIdAndViewedAtAfter(Long memberId, Long cityId, LocalDateTime afterTime);

//    @Query(value = "SELECT CONCAT(c.korName, '-', c.name) AS city, COUNT(*) AS viewCount " +
//            "FROM views v " +
//            "JOIN city c ON v.city_id = c.id " +
//            "WHERE YEAR(v.viewedAt) = YEAR(NOW()) " +
//            "AND c.del_yn = 'n'" +
//            "GROUP BY v.city_id " +
//            "ORDER BY viewCount DESC " +
//            "limit 5", nativeQuery = true)
//    List<YearlyCountryViewRatioInterface> countCityTopViewCounts();
}
