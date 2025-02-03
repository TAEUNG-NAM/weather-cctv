package live.narcy.weather.entity;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
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
    private City city;

    @Column(name = "del_yn")
    private char delYn;
}
