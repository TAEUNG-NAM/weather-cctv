package live.narcy.weather.member.entity;

import jakarta.persistence.*;
import live.narcy.weather.member.dto.MemberDto;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;

@Entity
@Table(name = "member")
@Getter
@RequiredArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String email;
    private String password;

    private String role;

    @CreatedDate
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Builder
    public Member(String email, String password, String role){
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public static Member from(MemberDto memberDTO, BCryptPasswordEncoder bCryptPasswordEncoder) {
        return Member.builder()
                .email(memberDTO.getEmail())
                .password(bCryptPasswordEncoder.encode(memberDTO.getPassword()))
                .role(memberDTO.getRole())
                .build();
    }
}
