package live.narcy.weather.city.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MonthlyViewCount {
    private int month;          // 월 (1~12)
    private long viewCount;     // 조회수
}
