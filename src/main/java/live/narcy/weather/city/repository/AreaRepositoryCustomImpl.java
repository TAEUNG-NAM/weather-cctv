package live.narcy.weather.city.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import live.narcy.weather.city.entity.Area;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static live.narcy.weather.city.entity.QArea.area;
import static live.narcy.weather.city.entity.QCity.city;

@Repository
@RequiredArgsConstructor
public class AreaRepositoryCustomImpl implements AreaRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<Area> findByCityName(String cityName) {
        return jpaQueryFactory
                .select(area)
                .from(area)
                .join(area.city, city).fetchJoin()
                .where(city.name.eq(cityName), area.delYn.eq("n"))
                .fetch();
    }
}
