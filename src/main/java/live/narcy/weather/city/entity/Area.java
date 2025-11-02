package live.narcy.weather.city.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import live.narcy.weather.city.dto.AreaDto;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

@Table(name = "area")
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class Area {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(name = "cctv_src")
    private String cctvSrc;

    @Column(name = "map_src")
    private String mapSrc;

//    @JsonIgnore     // 지연 로딩으로 인해 생성된 프록시 객체 클라이언트로 전달할 때 제외
    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "city_id")
    private City city;

    @Column(name = "del_yn", length = 1)
    @ColumnDefault("'n'")
    private String delYn;

    @Builder
    public Area( String name, String cctvSrc, String mapSrc, City city, String delYn) {
        this.name = name;
        this.cctvSrc = cctvSrc;
        this.mapSrc = mapSrc;
        this.city = city;
        this.delYn = delYn;
    }

    public Area patch(AreaDto area) {
        if(this.id != area.getId()) {
            throw new IllegalArgumentException("ID 불일치");
        }

        if(area.getName() != null) {
            this.name = area.getName();
        }
        if(area.getCctvSrc() != null) {
            this.cctvSrc = area.getCctvSrc();
        }
        if(area.getMapSrc() != null) {
            this.mapSrc = area.getMapSrc();
        }
        if(area.getDelYn() != null) {
            this.delYn = area.getDelYn();
        }

        return this;
    }

    public static Area from(AreaDto dto, City city) {
        return Area.builder()
                .name(dto.getName())
                .cctvSrc(dto.getCctvSrc())
                .mapSrc(dto.getMapSrc())
                .city(city)
                .delYn(dto.getDelYn())
                .build();
    }

}
