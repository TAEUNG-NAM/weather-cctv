package live.narcy.weather.config.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    CITY_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 도시를 찾을 수 없습니다."),
    AREA_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 구역을 찾을 수 없습니다."),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "잘못된 HTTP 메소드 입니다."),
    NO_PERMISSION(HttpStatus.UNAUTHORIZED, "권한이 없습니다."),
    CITY_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 등록된 도시입니다."),
    MISMATCH_ID(HttpStatus.BAD_REQUEST, "ID 불일치"),
    FAIL_CREATE_CITY(HttpStatus.BAD_REQUEST, "도시 등록/수정에 실패하였습니다.");

    private final HttpStatus httpStatus;
    private final String message;

}
