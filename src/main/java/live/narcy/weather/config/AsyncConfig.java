package live.narcy.weather.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfig implements AsyncConfigurer {

    /*
    1. 어플리케이션 시작 시 스레드 1개부터 setCorePoolSize(n)까지 점직적 생성(시작부터 지정된 갯수만큼 생성X)
    2. setQueueCapacity(n)이 가득 차면 setMaxPoolSize(n)까지 스레드 수 증설(큐 용량 안차면 스레드 수 증설X)
    */
    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(16);   // 기본 스레드 수(큐 용량이 다 차면 최대 스레드 수 만큼 증량)
        executor.setMaxPoolSize(32);    // 최대 스레드 수(default: Integer.MAX_VALUE)
        executor.setQueueCapacity(150); // 큐 용량(default: Integer.MAX_VALUE)
        executor.setThreadNamePrefix("ViewsExecutor-");
        executor.initialize();
        return executor;
    }
}
