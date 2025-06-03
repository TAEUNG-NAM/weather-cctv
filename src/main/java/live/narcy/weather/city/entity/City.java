package live.narcy.weather.city.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import java.util.Objects;

@Entity
@Getter
@NoArgsConstructor
public class City {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String country;
    private String name;
    @Column(unique = true)
    private String korName;
    private String thumbnail;
    @Column(name = "del_yn", length = 1)
    @ColumnDefault("'n'")
    private String delYn;

    @Builder
    public City(Long id, String country, String name, String korName, String delYn, String thumbnail) {
        this.id = id;
        this.country = country;
        this.name = name;
        this.korName = korName;
        this.delYn = delYn;
        this.thumbnail = thumbnail;
    }

    public City patch(City target) {
        if(!Objects.equals(this.id, target.id)) {
            throw new IllegalArgumentException("ID 불일치");
        }

        if(target.getName() != null) {
            this.name = target.getName();
        }
        if(target.getKorName() != null) {
            this.korName = target.getKorName();
        }
        if(target.delYn != null) {
            this.delYn = target.getDelYn();
        }

        return this;
    }
}
