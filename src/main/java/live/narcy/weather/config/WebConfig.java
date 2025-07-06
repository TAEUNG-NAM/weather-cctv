package live.narcy.weather.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 1. 서버 파일 경로 접근 설정
 * 2. 로깅 인터셉터 등록
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${file.upload.path}")
    private String uploadPath;
    @Value("${file.access.path}")
    private String accessPath;
    private LoggingInterceptor loggingInterceptor;

    public WebConfig(LoggingInterceptor loggingInterceptor) {
        this.loggingInterceptor = loggingInterceptor;
    }

    // 1
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry){
        registry.addResourceHandler(accessPath)
                .addResourceLocations(uploadPath);
    }

    // 2
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loggingInterceptor)
                .addPathPatterns("/**")             // 모든 요청에 적용
                .excludePathPatterns("/assets/**")  // 제외할 요청들
                .excludePathPatterns("/vendors/**")
                .excludePathPatterns("/js/**")
                .excludePathPatterns("/error/**")
                .excludePathPatterns("/thumbnail/**")
                .excludePathPatterns("/cctvLogin.html");
    }
}
