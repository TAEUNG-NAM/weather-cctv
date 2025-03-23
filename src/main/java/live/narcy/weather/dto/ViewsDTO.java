package live.narcy.weather.dto;

import live.narcy.weather.entity.Views;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ViewsDTO {
    private Long cityId;
    private Long memberId;
    private LocalDateTime viewedAt;

    @Builder
    public ViewsDTO(Long cityId, Long memberId, LocalDateTime viewedAt) {
        this.cityId = cityId;
        this.memberId = memberId;
        this.viewedAt = viewedAt;
    }

}
