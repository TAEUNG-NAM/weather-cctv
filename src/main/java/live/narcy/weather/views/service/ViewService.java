package live.narcy.weather.views.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import live.narcy.weather.views.dto.MonthlyViewCount;
import live.narcy.weather.views.dto.ViewsDto;
import live.narcy.weather.views.dto.YearlyCountryViewRatio;
import live.narcy.weather.city.entity.City;
import live.narcy.weather.member.entity.Member;
import live.narcy.weather.views.entity.Views;
import live.narcy.weather.city.repository.CityRepository;
import live.narcy.weather.member.repository.MemberRepository;
import live.narcy.weather.views.repository.ViewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
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
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    // 조회 정보 Redis Key
    private static final String VIEW_HISTORY_QUEUE_KEY = "view:history:queue";
    // 중복 방지용 키 Prefix
    private static final String VIEW_PREVENT_KEY_PREFIX = "view:prevent:";
    // 중복 방지 시간(5분)
    private static final long PREVENT_DURATION_MINUTES = 5;

    /**
     * 조회수 등록(회원)
     * @param email
     * @param city
     */
    public void recordViewHistory(String email, City city) {
        // 사용자 조회
        Optional<Member> optionalMember = memberRepository.findByEmail(email);
        optionalMember.ifPresent(member -> {
            long memberId = member.getId();
            long cityId = city.getId();

            ViewsDto view = ViewsDto.builder()
                    .memberId(memberId)
                    .cityId(cityId)
                    .viewedAt(LocalDateTime.now()).build();

            try {
                // 중복 방지 키 생성(ex view:prevent:10:3)
                String preventKey = VIEW_PREVENT_KEY_PREFIX + cityId + ":" + memberId;

                // 중복 여부 체크 후 세팅(SETNX 명령어)
                // setIfAbsent(SET key value NX EX seconds): 키가 없을 때만 true반환, 있으면 false
                Boolean isFirstView = redisTemplate.opsForValue()
                        .setIfAbsent(preventKey, "1", Duration.ofMinutes(PREVENT_DURATION_MINUTES));

                if (Boolean.TRUE.equals(isFirstView)) {
                    // DTO -> JSON
                    String viewJson = objectMapper.writeValueAsString(view);
                    // Redis List의 왼쪽에 데이터 PUSH (큐에 넣기)
                    redisTemplate.opsForList().leftPush(VIEW_HISTORY_QUEUE_KEY, viewJson);
                }
            } catch (Exception e) {
                log.error("Redis 큐 조회수 등록 중 에러 발생: {}", e.getMessage());
            }
        });

    }

    /**
     * 조회수 등록(비회원)
     * @param city
     */
    public void recordViewHistoryAnonymous(City city) {
        Long cityId = city.getId();
        ViewsDto view = ViewsDto.builder()
                .memberId(null)
                .cityId(cityId)
                .viewedAt(LocalDateTime.now()).build();

        try {
            // DTO -> JSON
            String viewJson = objectMapper.writeValueAsString(view);
            // Redis List의 왼쪽에 데이터 PUSH (큐에 넣기)
            redisTemplate.opsForList().leftPush(VIEW_HISTORY_QUEUE_KEY, viewJson);
        } catch (Exception e) {
            log.error("Redis 큐 조회수 등록 중 에러 발생: {}", e.getMessage());
        }
    }

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
                        .build();

                viewRepository.save(views);
            }
        } else {
            if(!isViewed) {
                Views views = Views.builder()
                        .member(null)
                        .city(city)
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
    @Transactional(readOnly = true)
    public List<MonthlyViewCount> getViewsCount(String year, String countryName, String cityName) {
        if("total".equals(cityName)) {
            // 전체 도시 조회일 때
            return viewRepository.countMonthlyCountryViewCounts(countryName, Integer.parseInt(year));
        } else {
            // 선택된 도시 조회일 때
            // 월별 조회수 조회
            return viewRepository.countMonthlyCityViewCounts(cityName, Integer.parseInt(year));
        }
    }

    /**
     * 선택된 (Year, Country)의 조회수 조회(DonutChart)
     * @param year
     * @param country
     * @return
     */
    @Transactional(readOnly = true)
    public List<YearlyCountryViewRatio> getViewsRatio(String year, String country) {
        return viewRepository.countYearlyCountryViewRatio(country, Integer.parseInt(year));
    }


    /**
     * 올해의 도시 조회수 조회
     * @return
     */
    @Transactional(readOnly = true)
    public Map<String, String> getTopCityViews() {
        List<YearlyCountryViewRatio> topViewCityList = viewRepository.countCityTopViewCounts();

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
