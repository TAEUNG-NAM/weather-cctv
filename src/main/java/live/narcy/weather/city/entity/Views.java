package live.narcy.weather.city.entity;

import jakarta.persistence.*;
import live.narcy.weather.member.entity.Member;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
public class Views {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "city_id")
    private live.narcy.weather.city.entity.City city;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    private LocalDateTime viewedAt;

    @Builder
    public Views(live.narcy.weather.city.entity.City city, Member member, LocalDateTime viewedAt) {
        this.city = city;
        this.member = member;
        this.viewedAt = viewedAt;
    }
}
