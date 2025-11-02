package live.narcy.weather.views.entity;

import jakarta.persistence.*;
import live.narcy.weather.city.entity.City;
import live.narcy.weather.member.entity.Member;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "views")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
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

    @CreatedDate
    @Column(name = "viewed_at")
    private LocalDateTime viewedAt;
    // 월별 랭킹 집계를 위한 컬럼(성능 최적화용)
//    private String viewdYearMonth;

    @Builder
    public Views(live.narcy.weather.city.entity.City city, Member member) {
        this.city = city;
        this.member = member;
    }
}
