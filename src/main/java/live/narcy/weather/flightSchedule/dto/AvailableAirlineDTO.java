package live.narcy.weather.flightSchedule.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
@NoArgsConstructor
public class AvailableAirlineDTO {

    @JsonProperty("AIRLINE_KOREAN")
    private String airlineKorean;

    @JsonProperty("AIRLINE_ENGLISH")
    private String airlineEnglish;

    @JsonProperty("AIRLINE_IDX")
    private String airlineIdx;

    @JsonProperty("AIRLINE_CODE2")
    private String airlineCode2;

    @JsonProperty("AIRLINE_CODE3")
    private String airlineCode3;

}
