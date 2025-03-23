package live.narcy.weather.entity;

import jakarta.persistence.*;
import lombok.Builder;

import java.time.LocalDateTime;

@Entity
public class Views {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "city_id")
    private City city;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    private LocalDateTime viewedAt;

    @Builder
    public Views(City city, Member member, LocalDateTime viewedAt) {
        this.city = city;
        this.member = member;
        this.viewedAt = viewedAt;
    }
}
