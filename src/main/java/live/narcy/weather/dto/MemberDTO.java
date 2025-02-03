package live.narcy.weather.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class MemberDTO {
    private Long id;
    private String email;
    private String role;

    @Builder
    public MemberDTO(Long id, String email, String role) {
        this.id = id;
        this.email = email;
        this.role = role;
    }
}
