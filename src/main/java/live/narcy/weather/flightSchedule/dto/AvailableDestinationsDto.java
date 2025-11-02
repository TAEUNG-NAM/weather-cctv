package live.narcy.weather.flightSchedule.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
public class AvailableDestinationsDto {
    @JsonProperty("CITY_CODE")
    private String cityCode;

    @JsonProperty("CITY_ENG")
    private String cityEng;

    @JsonProperty("CITY_KOR")
    private String cityKor;

    @Builder
    public AvailableDestinationsDto(String cityCode, String cityEng, String cityKor) {
        this.cityCode = cityCode;
        this.cityEng = cityEng;
        this.cityKor = cityKor;
    }
}
