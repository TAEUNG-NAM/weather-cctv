package live.narcy.weather.city.dto;

import live.narcy.weather.city.entity.City;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
public class CityDto {
    private Long id;
    private String engName;
    private String korName;
    private String country;
    private String delYn;
    private String thumbnail;

    @Builder
    public CityDto(Long id, String engName, String korName, String country, String delYn, String thumbnail) {
        this.id = id;
        this.engName = engName;
        this.korName = korName;
        this.country = country;
        this.delYn = delYn;
        this.thumbnail = thumbnail;
    }

    public static CityDto from(City city) {
        return CityDto.builder()
                .id(city.getId())
                .engName(city.getName())
                .korName(city.getKorName())
                .country(city.getCountry())
                .delYn(city.getDelYn())
                .thumbnail(city.getThumbnail())
                .build();
    }
}
