package live.narcy.weather.oauth2;

import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.stereotype.Component;

/**
 * 서비스별 OAuth2 클라이언트의 등록 정보를 가지는 클래스
 * OAuth2 Registration, Provider 설정
 */
@Component
public class CstmClientRegistration {

    public ClientRegistration naverClientRegistration() {
        return ClientRegistration.withRegistrationId("naver")
                .clientId("nRd5ijy1uU8vPzQI3UvE")                                 // ec2
                .clientSecret("VX09md7M0J")                                       // ec2
                .redirectUri("http://narcy.kro.kr:80/login/oauth2/code/naver")    // ec2

//                .clientId("iOAbkXpIFq3MU883yGyO")                                   // S10e
//                .clientSecret("")                                         // S10e
//                .redirectUri("http://narcy-dev.kro.kr:80/login/oauth2/code/naver")  // S10e

//                .clientId("8QXlYYdF49eekVEZr8ir")                                   // 로컬
//                .clientSecret("")                                         // 로컬
//                .redirectUri("http://localhost:8080/login/oauth2/code/naver")       // 로컬
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .scope("name", "email")
                .authorizationUri("https://nid.naver.com/oauth2.0/authorize")
                .tokenUri("https://nid.naver.com/oauth2.0/token")
                .userInfoUri("https://openapi.naver.com/v1/nid/me")
                .userNameAttributeName("response")
                .build();
    }

    public ClientRegistration kakaoClientRegistration() {
        return ClientRegistration.withRegistrationId("kakao")
                .clientId("52446f9d0570194b664c94bd0d18757a")
                .clientSecret("PGelh84kG2WOV8eYDTGMQhO1uMhgNVtC")
                .redirectUri("http://narcy.kro.kr:80/login/oauth2/code/kakao")
//                .redirectUri("http://localhost:8080/login/oauth2/code/kakao")
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .scope("profile_nickname", "account_email")
                .authorizationUri("https://kauth.kakao.com/oauth/authorize")
                .tokenUri("https://kauth.kakao.com/oauth/token")
                .userInfoUri("https://kapi.kakao.com/v2/user/me")
                .userNameAttributeName("kakao_account")
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_POST)      // 카카오만 설정
                .build();
    }
}
