package live.narcy.weather.views.scheduler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import live.narcy.weather.config.exception.CustomException;
import live.narcy.weather.config.exception.ErrorCode;
import live.narcy.weather.views.dto.ViewsDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class ViewScheduler {

    private final RedisTemplate<String, Object> redisTemplate;
    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper;

    // 조회 정보 Redis Key
    private static final String VIEW_HISTORY_QUEUE_KEY = "view:history:queue";
    // 한 번에 처리할 배치 사이즈view:history:queue
    private static final int BATCH_SIZE = 100;

    // 180초(3분) 마다 Flush
    @Scheduled(fixedDelay = 180000)
    public void flushViewHistory() {

        // Redis List에서 데이터를 BATCH_SIZE만큼 오른쪽에서 꺼냄
        // Pop이 동시성 이슈가 적음
        // rightPop(key, count)는 레디스 서버 6.2 이상부터 지원
        List<Object> viewJsonList = redisTemplate.opsForList().rightPop(VIEW_HISTORY_QUEUE_KEY, BATCH_SIZE);
        if (viewJsonList == null || viewJsonList.isEmpty()) {
            return;
        }

        List<ViewsDto> viewsDtos = viewJsonList.stream()
                .map(val -> {
                    try {
                        return objectMapper.readValue((String)val, ViewsDto.class); // JSON to DTO
                    } catch (JsonProcessingException e) {
                        log.error(String.valueOf(e));
                        throw new CustomException(ErrorCode.FAIL_JSON_PARSING);
                    }
                })
                .toList();

        String sql = "INSERT INTO views(city_id, member_id, viewed_at) VALUES(?, ?, ?)";

        // JPA saveAll: 엔티티를 1차 캐시에 넣고, 스냅샷을 만들고, 상태를 추적하여 느림 && 내부적으로 insert를 반복하여 N번 DB접근
        // batchUpdate: 상태 관리가 필요X, SQL을 직접 제어O, 대용량 데이터를 고성능으로 벌크 처리 -> 1번 DB접근
        jdbcTemplate.batchUpdate(sql,
                viewsDtos,
                BATCH_SIZE,
                (PreparedStatement ps, ViewsDto dto) -> {
                    ps.setLong(1, dto.getCityId());
                    // 비회원 조회 정보 일 때
                    if (dto.getMemberId() == null) {
                        ps.setNull(2, java.sql.Types.BIGINT);
                    } else {
                        ps.setLong(2, dto.getMemberId());
                    }
                    ps.setTimestamp(3, Timestamp.valueOf(dto.getViewedAt()));
        });

        log.info("Redis Flushed-조회 정보 {}건 DB반영", viewsDtos.size());
    }

}
