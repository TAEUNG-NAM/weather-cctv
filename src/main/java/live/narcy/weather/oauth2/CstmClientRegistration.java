package live.narcy.weather.oauth2;

import org.springframework.beans.factory.annotation.Value;
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

    @Value("${oauth2.naver.client.id}")
    private String naverClientId;
    @Value("${oauth2.naver.client.secret}")
    private String naverClientSecret;
    @Value("${oauth2.kakao.client.id}")
    private String kakaoClientId;
    @Value("${oauth2.kakao.client.secret}")
    private String kakaoClientSecret;


    public ClientRegistration naverClientRegistration() {
        return ClientRegistration.withRegistrationId("naver")
                .clientId(naverClientId)                                            // prod
                .clientSecret(naverClientSecret)                                    // prod
                .redirectUri("http://narcy.kro.kr:80/login/oauth2/code/naver")      // prod

//                .clientId("iOAbkXpIFq3MU883yGyO")                                   // dev
//                .clientSecret("")                                                   // dev
//                .redirectUri("http://narcy-dev.kro.kr:80/login/oauth2/code/naver")  // dev

//                .clientId("8QXlYYdF49eekVEZr8ir")                                   // 로컬
//                .clientSecret("ksW4vGY6oi")                                         // 로컬
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
                .clientId(kakaoClientId)
                .clientSecret(kakaoClientSecret)
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
