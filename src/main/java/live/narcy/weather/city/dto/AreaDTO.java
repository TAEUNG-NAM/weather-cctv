package live.narcy.weather.city.dto;

import live.narcy.weather.city.entity.Area;
import live.narcy.weather.city.entity.City;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class AreaDTO {

    private Long id;
    private String name;
    private String cctvSrc;
    private String mapSrc;
    private String city;
    private String delYn;

    @Builder
    public AreaDTO(Long id, String name, String cctvSrc, String mapSrc, String city, String delYn) {
        this.id = id;
        this.name = name;
        this.cctvSrc = cctvSrc;
        this.mapSrc = mapSrc;
        this.city = city;
        this.delYn = delYn;
    }

    public Area toArea(City city) {
        return Area.builder()
                .name(this.getName())
                .cctvSrc(this.getCctvSrc())
                .mapSrc(this.getMapSrc())
                .city(city)
                .delYn(this.getDelYn())
                .build();

    }
}
