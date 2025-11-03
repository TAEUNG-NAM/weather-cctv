package live.narcy.weather.oauth2;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.SupplierClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.HttpSessionOAuth2AuthorizedClientRepository;

/**
 * ClientRegistration의 저장소, 서비스별 ClientRegistration를 가짐.
 */
@Configuration
@RequiredArgsConstructor
public class CstmClientRegistrationRepository {

    private final CstmClientRegistration cstmClientRegistration;

    @Bean
    public ClientRegistrationRepository clientRegistrationRepository() {

        // InMemory 타입의 저장소(JDBC-DB 타입도 존재)
        return new InMemoryClientRegistrationRepository(cstmClientRegistration.naverClientRegistration(), cstmClientRegistration.kakaoClientRegistration());  // ClientRegistration을 담아 Repository 생성 및 반환
    }
}
