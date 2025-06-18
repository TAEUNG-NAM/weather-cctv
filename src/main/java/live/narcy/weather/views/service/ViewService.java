package live.narcy.weather.views.service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import live.narcy.weather.views.dto.MonthlyViewCountInterface;
import live.narcy.weather.views.dto.YearlyCountryViewRatio;
import live.narcy.weather.views.dto.YearlyCountryViewRatioInterface;
import live.narcy.weather.city.entity.City;
import live.narcy.weather.member.entity.Member;
import live.narcy.weather.views.entity.Views;
import live.narcy.weather.city.repository.CityRepository;
import live.narcy.weather.member.repository.MemberRepository;
import live.narcy.weather.views.repository.ViewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ViewService {

    private final ViewRepository viewRepository;
    private final MemberRepository memberRepository;
    private final CityRepository cityRepository;

    /**
     * 조회수 증가(DB저장)
     * @param email
     * @param city
     */
    @Async
    public void increaseViewCount(String email, City city, Boolean isViewed) {

        Optional<Member> optionalMember = memberRepository.findByEmail(email);

        // 멤버 조회 및 조회수 증가 로직
        log.info("비동기 스레드: {}", Thread.currentThread().getName());

        if(optionalMember.isPresent()) {
            Member member = optionalMember.get();
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
        } else {
            if(!isViewed) {
                Views views = Views.builder()
                        .member(null)
                        .city(city)
                        .viewedAt(LocalDateTime.now())
                        .build();

                viewRepository.save(views);
            }
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

        return monthlyViewCountList;
    }

    /**
     * 선택된 (Year, Country)의 조회수 조회(DonutChart)
     * @param year
     * @param country
     * @return
     */
    public List<YearlyCountryViewRatioInterface> getViewsRatio(String year, String country) {

        List<YearlyCountryViewRatioInterface> yearlyViewsRatioMap = viewRepository.countYearlyCountryViewRatio(country, Integer.parseInt(year));

        return yearlyViewsRatioMap;
    }


    public Map<String, String> getTopCityViews() {
        List<YearlyCountryViewRatioInterface> topViewCityList = viewRepository.countCityTopViewCounts();

        Map<String, String> topViewCities = new HashMap<>();
        topViewCities.forEach((k, v) -> {
            System.out.println("k = " + k + "v = " + v);
        });

        for(int i=1; i <= topViewCityList.size(); i++) {
            String[] cityName = topViewCityList.get(i-1).getCity().split("-");
            topViewCities.put("topCity_"+i, cityName[0]);
            topViewCities.put("topCityEng_"+i, cityName[1]);
            topViewCities.put("topView_"+i, String.valueOf(topViewCityList.get(i-1).getViewCount()));
        }

        return topViewCities;
    }
}
