package live.narcy.weather.dto;

public interface OAuth2Response {

    // 제공자(naver, kakao, google)
    String getProvider();

    // 제공자에서 발급한 ID(번호)
    String getProviderId();

    // 이메일
    String getEmail();

    // 사용자 이름
    String getName();
}
