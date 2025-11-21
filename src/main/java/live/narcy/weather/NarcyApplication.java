package live.narcy.weather;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

import javax.xml.stream.events.Namespace;
import java.util.TimeZone;

@SpringBootApplication
@EnableJpaAuditing
@EnableRedisHttpSession(redisNamespace = "${spring.session.redis.namespace}")
@EnableScheduling
public class NarcyApplication {

    public static void main(String[] args) {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));    // 타임존 설정

        SpringApplication.run(NarcyApplication.class, args);
    }

}
