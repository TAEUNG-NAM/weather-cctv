package live.narcy.weather.member.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Entity
@Getter
@RequiredArgsConstructor
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

//    @Column(unique = true)
    private String email;
    private String password;

    private String role;

    @Builder
    public Member(String email, String password, String role){
        this.email = email;
        this.password = password;
        this.role = role;
    }
}
