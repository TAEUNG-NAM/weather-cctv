package live.narcy.weather.member.dto;

import java.util.Map;

public class KakaoResponse implements OAuth2Response{

    private final Map<String, Object> attribute;

    public KakaoResponse(Map<String, Object> attribute) {
        this.attribute = attribute;
    }

    @Override
    public String getProvider() {
        return "kakao";
    }

    @Override
    public String getProviderId() {
        return attribute.get("id").toString();
    }

    @Override
    public String getEmail() {
        Map<String, Object> kakao_account =  (Map<String, Object>)attribute.get("kakao_account");
        return kakao_account.get("email").toString();
    }

//    @Override
//    public String getName() {
//        Map<String, Object> properties =  (Map<String, Object>)attribute.get("properties");
//        return properties.get("nickname").toString();
//    }
}
