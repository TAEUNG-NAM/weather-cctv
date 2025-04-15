package live.narcy.weather.member.service;

import live.narcy.weather.member.dto.CustomUserDetails;
import live.narcy.weather.member.entity.Member;
import live.narcy.weather.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        System.out.println("email = " + email);

        Member memberData = memberRepository.findByEmail(email);

        if(memberData != null) {
            return new CustomUserDetails(memberData);
        }

        return null;
    }
}
