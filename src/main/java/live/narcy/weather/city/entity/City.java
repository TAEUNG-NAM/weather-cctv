package live.narcy.weather.city.entity;

import jakarta.persistence.*;
import live.narcy.weather.city.dto.CityDto;
import live.narcy.weather.config.exception.CustomException;
import live.narcy.weather.config.exception.ErrorCode;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "city")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class City {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String country;

    @Column(unique = true)
    private String name;

    @Column(unique = true)
    private String korName;

    private String thumbnail;

    @Column(name = "del_yn", length = 1)
    @ColumnDefault("'n'")
    private String delYn;

    @OneToMany(mappedBy = "city", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Area> areas = new ArrayList<>();

    @Builder
    public City(Long id, String country, String name, String korName, String delYn, String thumbnail) {
        this.id = id;
        this.country = country;
        this.name = name;
        this.korName = korName;
        this.delYn = delYn;
        this.thumbnail = thumbnail;
    }

    public City patch(CityDto target) {
        if(!Objects.equals(this.id, target.getId())) {
            throw new CustomException(ErrorCode.MISMATCH_ID);
        }
        if(target.getEngName() != null) {
            this.name = target.getEngName();
        }
        if(target.getKorName() != null) {
            this.korName = target.getKorName();
        }
        if(target.getDelYn() != null) {
            this.delYn = target.getDelYn();
        }

        return this;
    }

    public void addArea(Area area) {
        this.areas.add(area);
        area.setCity(this);
    }
}
