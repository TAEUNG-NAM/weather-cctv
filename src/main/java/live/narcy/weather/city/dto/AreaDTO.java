package live.narcy.weather.city.dto;

import live.narcy.weather.city.entity.Area;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AreaDTO {

    private Long id;
    private String name;
    private String cctvSrc;
    private String mapSrc;

    @Builder
    public AreaDTO(Long id, String name, String cctvSrc, String mapSrc) {
        this.id = id;
        this.name = name;
        this.cctvSrc = cctvSrc;
        this.mapSrc = mapSrc;
    }

    public static AreaDTO toDTO(Area area) {
        return AreaDTO.builder()
                .id(area.getId())
                .name(area.getName())
                .cctvSrc(area.getCctvSrc())
                .mapSrc(area.getMapSrc())
                .build();

    }
}
