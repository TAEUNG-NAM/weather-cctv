package live.narcy.weather.views.repository;

import live.narcy.weather.views.dto.MonthlyViewCount;
import live.narcy.weather.views.dto.YearlyCountryViewRatio;

import java.util.List;

public interface ViewRepositoryCustom {
    List<YearlyCountryViewRatio> countYearlyCountryViewRatio(String country, int year);
    List<MonthlyViewCount> countMonthlyCountryViewCounts(String country, int year);
    List<MonthlyViewCount> countMonthlyCityViewCounts(String cityName, int year);
    List<YearlyCountryViewRatio> countCityTopViewCounts();
}
