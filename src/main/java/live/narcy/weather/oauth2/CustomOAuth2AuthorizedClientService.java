package live.narcy.weather.oauth2;

import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.oauth2.client.JdbcOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;

/** OAuth2 소셜 로그인을 진행하는 사용자에 대해 인증 서버에서 발급 받은 Access 토큰과 같은 정보를 담을 저장소가 필요(기본적으로 인메모리 방식으로 관리)
소셜 로그인 사용자 수가 증가&서버의 스케일 아웃 문제로 인해 인메모리 방식은 실무에서 사용X -> DB에 저장하는 방식으로 커스텀 */
@Configuration
public class CustomOAuth2AuthorizedClientService {

    public OAuth2AuthorizedClientService oAuth2AuthorizedClientService(JdbcTemplate jdbcTemplate, ClientRegistrationRepository clientRegistrationRepository) {
        return new JdbcOAuth2AuthorizedClientService(jdbcTemplate, clientRegistrationRepository);
    }
}
