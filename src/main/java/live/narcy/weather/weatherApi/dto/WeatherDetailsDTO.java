package live.narcy.weather.weatherApi.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@ToString
public class WeatherDetailsDTO {
    double temp;
    String description;
    String icon;
    LocalDateTime date;

    @Builder
    public WeatherDetailsDTO(double temp, String description, String icon, LocalDateTime date) {
        this.temp = temp;
        this.description = description;
        this.icon = icon;
        this.date = date;
    }
}
