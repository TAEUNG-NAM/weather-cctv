package live.narcy.weather.service;

import live.narcy.weather.dto.*;
import live.narcy.weather.entity.Member;
import live.narcy.weather.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final MemberRepository memberRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User =  super.loadUser(userRequest);
        System.out.println(oAuth2User.getAttributes());
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

        Member findMember = memberRepository.findByEmail(oAuth2Response.getEmail());

        // 계정 정보가 DB에 없을 때
        if(findMember == null) {
            MemberDTO memberDTO = MemberDTO.builder()
                    .email(oAuth2Response.getEmail())
                    .password("oauth2pw")
                    .role("ROLE_USER")
                    .build();

            Member newMember = MemberDTO.toMember(memberDTO, new BCryptPasswordEncoder());

            memberRepository.save(newMember);
        } else {
            System.out.println("이미 가입한 회원");
            System.out.println("Email: " + oAuth2Response.getEmail() + "Role: " + findMember.getRole());
        }

        return new CustomOAuth2User(oAuth2Response, role);
    }
}
