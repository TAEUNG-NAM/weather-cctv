package live.narcy.weather.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;

/**
 * 인증 서버 토큰을 저장하지 않기 위해
 * 아무 동작도 하지 않는 "No-Op" Repository를 빈으로 등록
 */
@Configuration
public class NoOpAuthorizedClientRepositoryConfig {

    @Bean
    public OAuth2AuthorizedClientRepository oAuth2AuthorizedClientRepository() {
        return new NoOpAuthorizedClientRepository();
    }

    /**
     * OAuth2AuthorizedClientRepository의 'do-nothing' 구현체
     */
    static class NoOpAuthorizedClientRepository implements OAuth2AuthorizedClientRepository {

        @Override
        public <T extends OAuth2AuthorizedClient> T loadAuthorizedClient(String clientRegistrationId, Authentication principal, HttpServletRequest request) {
            // 토큰을 로드X
            return null;
        }

        @Override
        public void saveAuthorizedClient(OAuth2AuthorizedClient authorizedClient, Authentication principal, HttpServletRequest request, HttpServletResponse response) {
            // 토큰을 저장X
        }

        @Override
        public void removeAuthorizedClient(String clientRegistrationId, Authentication principal, HttpServletRequest request, HttpServletResponse response) {
            // 토큰을 삭제X
        }
    }
}