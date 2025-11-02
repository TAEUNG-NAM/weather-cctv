package live.narcy.weather.member.dto;

import live.narcy.weather.member.entity.Member;
import lombok.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class MemberDto {
    private String email;
    private String password;
    private String role = "ROLE_USER";

    @Builder
    public MemberDto(String email, String password, String role) {
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public static Member toMember(MemberDto memberDTO, BCryptPasswordEncoder bCryptPasswordEncoder) {
        return Member.builder()
                .email(memberDTO.email)
                .password(bCryptPasswordEncoder.encode(memberDTO.getPassword()))
                .role(memberDTO.role)
                .build();
    }
}
