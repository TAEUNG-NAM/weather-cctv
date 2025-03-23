package live.narcy.weather.service;

import live.narcy.weather.entity.City;
import live.narcy.weather.entity.Member;
import live.narcy.weather.entity.Views;
import live.narcy.weather.repository.MemberRepository;
import live.narcy.weather.repository.ViewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ViewService {

    private final ViewRepository viewRepository;
    private final MemberRepository memberRepository;

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

}
