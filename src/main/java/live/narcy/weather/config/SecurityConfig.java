package live.narcy.weather.config;

import live.narcy.weather.oauth2.CstmClientRegistrationRepository;
import live.narcy.weather.service.CustomOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.session.HttpSessionEventPublisher;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;
    private final CstmClientRegistrationRepository  clientRegistrationRepository;

    // 정적 자원(html, css, js) SecurityFilter 검증 제외
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.ignoring()
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations())
                .requestMatchers("/assets/**", "/js/**", "/vendors/**");
    }

    // 암호화 클래스
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 세션 생성/파기 관리자
    @Bean
    public HttpSessionEventPublisher httpSessionEventPublisher() {
        return new HttpSessionEventPublisher();
    }

    @Bean
    public SecurityFilterChain filter(HttpSecurity http) throws Exception{

        // 접근 권한 설정
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/home", "/login/**", "/join", "/joinProc", "/oauth2/**").permitAll()
                        .requestMatchers("/admin").hasRole("ADMIN")
                        .anyRequest().authenticated()
                );

        // 로그인 페이지 설정
        http
                .formLogin(auth -> auth.disable())
                        .csrf(auth -> auth.disable())
                                .httpBasic(auth -> auth.disable());

        // OAuth2 설정
        http
                .oauth2Login(oauth2 -> oauth2
                        .clientRegistrationRepository(clientRegistrationRepository.clientRegistrationRepository())  // class 방식을 통한 OAuth2 설정
                        .userInfoEndpoint(userInfoEndpointConfig -> userInfoEndpointConfig  // User정보 받을 EndPoint설정
                                .userService(customOAuth2UserService)));

        // 로그인 페이지 설정
//        http
//                .formLogin(auth -> auth
//                        .usernameParameter("email")     // CustomUsername 설정
//                        .loginPage("/login")
//                        .loginProcessingUrl("/loginProc")
//                        .permitAll()
//                );

        // 로그아웃 설정
        http
                .logout(auth -> auth
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/")
                );


        // 세션 설정
        http
                .sessionManagement(session -> session
                        .maximumSessions(1)                 // 다중 로그인
                        .maxSessionsPreventsLogin(false)    // 동시 로그인 시 기존 계정 로그아웃

                );

        return http.build();
    }
}
