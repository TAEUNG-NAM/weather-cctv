package live.narcy.weather.config;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import live.narcy.weather.member.dto.CustomOAuth2User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 로깅 인터셉터
 */
@Slf4j
@Component
public class LoggingInterceptor implements HandlerInterceptor {

    private static final String START_TIME = "startTime";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        request.setAttribute(START_TIME, System.currentTimeMillis());
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        long startTime = (Long) request.getAttribute(START_TIME);
        long duration = System.currentTimeMillis() - startTime;

        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null) {
            ip = request.getRemoteAddr();
        }
        // X-Forwarded-For가 여러 IP면, 맨 앞 IP만 사용
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }

        String email = "anonymous";
        Authentication authentication = (Authentication) request.getUserPrincipal();
        if (authentication != null && authentication.getPrincipal() instanceof CustomOAuth2User) {  // Authentication을 통해 추출
            email = ((CustomOAuth2User) authentication.getPrincipal()).getEmail();
        }

        String method = request.getMethod();
        String uri = request.getRequestURI();
        int status = response.getStatus();

        log.info("[email={}, ip={}] {} {} {} ({}ms)", email, ip, method, uri, status, duration);
    }
}
