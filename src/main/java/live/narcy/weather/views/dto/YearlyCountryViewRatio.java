package live.narcy.weather.views.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class YearlyCountryViewRatio {
    private String city;        // 도시명
    private long viewCount;     // 조회수
}
