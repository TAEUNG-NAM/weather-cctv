package live.narcy.weather.member.service;

import live.narcy.weather.member.dto.MemberDTO;
import live.narcy.weather.member.entity.Member;
import live.narcy.weather.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JoinService {

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    /**
     * 회원가입
     * @param dto
     */
    public void joinProcess(MemberDTO dto) {
        // DB에 중복 검증
        Boolean isExist = memberRepository.existsByEmail(dto.getEmail());
        if(isExist) {
            return;
        }

        // DTO -> Entity
        Member member = MemberDTO.toMember(dto, bCryptPasswordEncoder);

        memberRepository.save(member);
    }
}
