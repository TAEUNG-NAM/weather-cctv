package live.narcy.weather.views.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import live.narcy.weather.views.dto.MonthlyViewCount;
import live.narcy.weather.views.dto.YearlyCountryViewRatio;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

import static live.narcy.weather.city.entity.QCity.*;
import static live.narcy.weather.views.entity.QViews.*;

@Repository
@RequiredArgsConstructor
public class ViewRepositoryCustomImpl implements ViewRepositoryCustom{

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<YearlyCountryViewRatio> countYearlyCountryViewRatio(String country, int year) {
        return jpaQueryFactory
                .select(Projections.constructor(
                        YearlyCountryViewRatio.class,
                        city.name.as("city"),
                        views.count().as("viewCount")))
                .from(views)
                .join(views.city, city)
                .where(views.viewedAt.year().eq(year), city.country.eq(country))
                .groupBy(city.name)
                .orderBy(views.count().desc())
                .fetch();
    }

    @Override
    public List<MonthlyViewCount> countMonthlyCountryViewCounts(String country, int year) {
        BooleanExpression countryCondition = city.country.eq(country);

        return fetchMonthlyViewCounts(year, countryCondition);
    }

    @Override
    public List<MonthlyViewCount> countMonthlyCityViewCounts(String cityName, int year) {
        BooleanExpression cityCondition = city.name.eq(cityName);
        return fetchMonthlyViewCounts(year, cityCondition);
    }

    @Override
    public List<YearlyCountryViewRatio> countCityTopViewCounts() {
        return jpaQueryFactory
                .select(Projections.constructor(YearlyCountryViewRatio.class,
                        city.korName.concat("-").concat(city.name).as("city"),
                        views.count().as("viewCount")))
                .from(views)
                .join(views.city, city)
                .where(views.viewedAt.year().eq(LocalDate.now().getYear()), city.delYn.eq("n"))
                .groupBy(views.city)
                .orderBy(views.count().desc())
                .limit(5)
                .fetch();
    }

    public List<MonthlyViewCount> fetchMonthlyViewCounts(int year, BooleanExpression condition) {
        return jpaQueryFactory
                .select(Projections.constructor(MonthlyViewCount.class,
                        views.viewedAt.month().as("month"),
                        views.count().as("viewCount")))
                .from(views)
                .join(views.city, city)
                .where(
                        views.viewedAt.year().eq(year),
                        condition
                )
                .groupBy(views.viewedAt.month())
                .orderBy(views.viewedAt.month().asc())
                .fetch();
    }

}
