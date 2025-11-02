package live.narcy.weather.city.dto;

import live.narcy.weather.city.entity.Area;
import live.narcy.weather.city.entity.City;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class AreaDto {

    private Long id;
    private String name;
    private String cctvSrc;
    private String mapSrc;
    private String city;
    private String delYn;

    @Builder
    public AreaDto(Long id, String name, String cctvSrc, String mapSrc, String city, String delYn) {
        this.id = id;
        this.name = name;
        this.cctvSrc = cctvSrc;
        this.mapSrc = mapSrc;
        this.city = city;
        this.delYn = delYn;
    }

    public static AreaDto from(Area area) {
        return AreaDto.builder()
                .id(area.getId())
                .name(area.getName())
                .cctvSrc(area.getCctvSrc())
                .mapSrc(area.getMapSrc())
                .city(area.getCity().getName())
                .delYn(area.getDelYn())
                .build();

    }
}
