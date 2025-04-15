package live.narcy.weather.city.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import live.narcy.weather.city.dto.AreaDTO;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Area {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(name = "cctv_src")
    private String cctvSrc;

    @Column(name = "map_src")
    private String mapSrc;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "city_id")
    @JsonIgnore     // 지연 로딩으로 인해 생성된 프록시 객체 클라이언트로 전달할 때 제외
    private City city;

    @Builder
    public Area(Long id, String name, String cctvSrc, String mapSrc, City city) {
        this.id = id;
        this.name = name;
        this.cctvSrc = cctvSrc;
        this.mapSrc = mapSrc;
        this.city = city;
    }

    public Area patch(AreaDTO area) {
        if(this.id != area.getId()) {
            throw new IllegalArgumentException("id 불일치");
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

        return this;
    }

}
