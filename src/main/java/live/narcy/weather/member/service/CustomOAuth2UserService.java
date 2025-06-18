package live.narcy.weather.member.service;

import live.narcy.weather.member.entity.Member;
import live.narcy.weather.member.dto.*;
import live.narcy.weather.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final MemberRepository memberRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User =  super.loadUser(userRequest);
        log.info("oAuth2UserAttributes = {}", oAuth2User.getAttributes());
        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        OAuth2Response oAuth2Response = null;

        if(registrationId.equals("naver")) {
            oAuth2Response = new NaverResponse(oAuth2User.getAttributes());

        } else if(registrationId.equals("kakao")) {
            oAuth2Response = new KakaoResponse(oAuth2User.getAttributes());

        } else {
            return null;
        }

        String role = "ROLE_USER";

        Optional<Member> findMember = memberRepository.findByEmail(oAuth2Response.getEmail());

        // 계정 정보가 DB에 없을 때
        if(findMember.isEmpty()) {
            MemberDTO memberDTO = MemberDTO.builder()
                    .email(oAuth2Response.getEmail())
                    .password("oauth2pw")
                    .role("ROLE_USER")
                    .build();

            Member newMember = MemberDTO.toMember(memberDTO, new BCryptPasswordEncoder());

            memberRepository.save(newMember);
            log.info("회원가입 = {}", memberDTO);
        } else {
            role = findMember.get().getRole();
            log.info("Email = {}\tRole = {}", oAuth2Response.getEmail(), role);
        }

        return new CustomOAuth2User(oAuth2Response, role);
    }
}
