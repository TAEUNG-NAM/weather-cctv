package live.narcy.weather.dto;

import live.narcy.weather.entity.Member;
import lombok.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class MemberDTO {
    private String email;
    private String password;
    private String role = "ROLE_USER";

    @Builder
    public MemberDTO(String email, String password, String role) {
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public static Member toMember(MemberDTO memberDTO, BCryptPasswordEncoder bCryptPasswordEncoder) {
        return Member.builder()
                .email(memberDTO.email)
                .password(bCryptPasswordEncoder.encode(memberDTO.getPassword()))
                .role(memberDTO.role)
                .build();
    }
}
