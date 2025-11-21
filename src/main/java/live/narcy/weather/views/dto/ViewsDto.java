package live.narcy.weather.views.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class ViewsDto {
    private Long cityId;
    private Long memberId;
    private LocalDateTime viewedAt;

    @Builder
    public ViewsDto(Long cityId, Long memberId, LocalDateTime viewedAt) {
        this.cityId = cityId;
        this.memberId = memberId;
        this.viewedAt = viewedAt;
    }

}
