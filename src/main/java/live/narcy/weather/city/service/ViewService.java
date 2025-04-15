package live.narcy.weather.city.service;

import live.narcy.weather.city.dto.MonthlyViewCount;
import live.narcy.weather.city.dto.MonthlyViewCountInterface;
import live.narcy.weather.city.dto.YearlyCountryViewRatio;
import live.narcy.weather.city.dto.YearlyCountryViewRatioInterface;
import live.narcy.weather.city.entity.City;
import live.narcy.weather.member.entity.Member;
import live.narcy.weather.city.entity.Views;
import live.narcy.weather.city.repository.CityRepository;
import live.narcy.weather.member.repository.MemberRepository;
import live.narcy.weather.city.repository.ViewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ViewService {

    private final ViewRepository viewRepository;
    private final MemberRepository memberRepository;
    private final CityRepository cityRepository;

    /**
     * 조회수 증가(DB저장)
     * @param email
     * @param city
     */
    public void increaseViewCount(String email, City city) {

        Member member = memberRepository.findByEmail(email);
        Long memberId = member.getId();
        
        LocalDateTime coolDownLimit = LocalDateTime.now().minusMinutes(5);  // 조회수 증가 쿨타임(5분)

        boolean hasRecentView = viewRepository.existsByMemberIdAndCityIdAndViewedAtAfter(memberId, city.getId(), coolDownLimit);

        if(!hasRecentView) {
            Views views = Views.builder()
                    .member(member)
                    .city(city)
                    .viewedAt(LocalDateTime.now())
                    .build();

            viewRepository.save(views);
        }

    }

    /**
     * 선택된 (Year, Country, City)의 조회수 조회(AreaChart)
     * @param year
     * @param countryName
     * @param cityName
     * @return
     */
    public List<MonthlyViewCountInterface> getViewsCount(String year, String countryName, String cityName) {

        List<MonthlyViewCountInterface> monthlyViewCountList;

        if("total".equals(cityName)) {
            // 전체 도시 조회일 때
            monthlyViewCountList = viewRepository.countMonthlyCountryViewCounts(countryName, Integer.parseInt(year));

        } else {
            // 선택된 도시 조회일 때
            // 월별 조회수 조회
            City city = cityRepository.findByName(cityName);
            monthlyViewCountList = viewRepository.countMonthlyCityViewCounts(city.getId(), Integer.parseInt(year));
        }


//        // 월별 조회수를 Map에 저장
//        Map<Integer, Long> monthlyViewCount = new LinkedHashMap<>();
//        for(int i = 1; i <= 12; i++) {
//            monthlyViewCount.put(i, 0L);    // 조회수 기본값 0으로 초기화
//        }
//
//        for(MonthlyViewCount row : monthlyViewCountList) {
//            Integer month = row.getMonth();       // 월
//            Long count = row.getViewCount();             // 조회수
//            monthlyViewCount.put(month, count);
//        }

        return monthlyViewCountList;
    }

    public List<YearlyCountryViewRatioInterface> getViewsRatio(String year, String country) {

        List<YearlyCountryViewRatioInterface> yearlyViewsRatioMap = viewRepository.countYearlyCountryViewRatio(country, Integer.parseInt(year));

        return yearlyViewsRatioMap;
    }

}
