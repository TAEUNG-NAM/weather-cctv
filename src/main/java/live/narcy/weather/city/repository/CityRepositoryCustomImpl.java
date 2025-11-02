package live.narcy.weather.city.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import live.narcy.weather.city.dto.CityDto;
import live.narcy.weather.city.entity.City;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static live.narcy.weather.city.entity.QArea.area;
import static live.narcy.weather.city.entity.QCity.city;

@Repository
@RequiredArgsConstructor
public class CityRepositoryCustomImpl implements CityRepositoryCustom{
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<CityDto> findCitiesByCountryName(String name) {
        return jpaQueryFactory
                .select(Projections.constructor(
                        CityDto.class,
                        city.id.as("id"),
                        city.name.as("engName"),
                        city.korName.as("korName"),
                        city.country.as("country"),
                        city.delYn.as("delYn"),
                        city.thumbnail.as("thumbnail")
                        ))
                .from(city)
                .where(city.country.eq(name), city.delYn.eq("n"))
                .fetch();
    }

    @Override
    public List<CityDto> findAllCitiesByCountryName(String name) {
        return jpaQueryFactory
                .select(Projections.constructor(
                        CityDto.class,
                        city.id.as("id"),
                        city.name.as("engName"),
                        city.korName.as("korName"),
                        city.country.as("country"),
                        city.delYn.as("delYn"),
                        city.thumbnail.as("thumbnail")
                ))
                .from(city)
//                .leftJoin(city.areas, area).fetchJoin()
                .where(city.country.eq(name))
                .fetch();
    }
}
