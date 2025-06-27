package live.narcy.weather.flightSchedule.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class FlightSchedulesDTO {
    @JsonProperty("AGENT_CD")
    private String agentCd;
    @JsonProperty("AIRLINE_ENGLISH")
    private String airlineEnglish;
    @JsonProperty("AIRLINE_HOMEPAGE_URL")
    private String airlineHomepageUrl;
    @JsonProperty("AIRLINE_KOREAN")
    private String airlineKorean;
    @JsonProperty("AIRPORT")
    private String airport;
    @JsonProperty("AIRPORT_ENG")
    private String airportEng;
    @JsonProperty("C_TIME")
    private String cTime;
    @JsonProperty("CITY")
    private String city;
    @JsonProperty("CITY_ENG")
    private String cityEng;
    @JsonProperty("INOUT_TYPE")
    private String inoutType;
    @JsonProperty("INTERNATIONAL_AIRPORT_DOME")
    private String internationalAirportDome;
    @JsonProperty("INTERNATIONAL_AIRPORT_INTE")
    private String internationalAirportInte;
    @JsonProperty("INTERNATIONAL_BYPASS")
    private String internationalBypass;
    @JsonProperty("INTERNATIONAL_BYPASS_ENG")
    private String internationalBypassEng;
    @JsonProperty("INTERNATIONAL_STDT")
    private String internationalStdt;
    @JsonProperty("INTERNATIONAL_EDDT")
    private String internationalEddt;
    @JsonProperty("INTERNATIONAL_IDX")
    private String internationalIdx;
    @JsonProperty("INTERNATIONAL_IO_TYPE")
    private String internationalIoType;
    @JsonProperty("INTERNATIONAL_NUM")
    private String internationalNum;
    @JsonProperty("INTERNATIONAL_TIME")
    private String internationalTime;
    @JsonProperty("INTERNATIONAL_MON")
    private String internationalMon;
    @JsonProperty("INTERNATIONAL_TUE")
    private String internationalTue;
    @JsonProperty("INTERNATIONAL_WED")
    private String internationalWed;
    @JsonProperty("INTERNATIONAL_THU")
    private String internationalThu;
    @JsonProperty("INTERNATIONAL_FRI")
    private String internationalFri;
    @JsonProperty("INTERNATIONAL_SAT")
    private String internationalSat;
    @JsonProperty("INTERNATIONAL_SUN")
    private String internationalSun;

    @Builder
    public FlightSchedulesDTO(String internationalSun, String internationalSat, String internationalFri, String internationalThu, String internationalWed, String internationalTue, String internationalMon, String internationalTime, String internationalNum, String internationalEddt, String internationalStdt, String airlineKorean, String city, String airlineEnglish, String airport, String internationalIdx, String airlineHomepageUrl) {
        this.internationalSun = internationalSun;
        this.internationalSat = internationalSat;
        this.internationalFri = internationalFri;
        this.internationalThu = internationalThu;
        this.internationalWed = internationalWed;
        this.internationalTue = internationalTue;
        this.internationalMon = internationalMon;
        this.internationalTime = internationalTime;
        this.internationalNum = internationalNum;
        this.internationalEddt = internationalEddt;
        this.internationalStdt = internationalStdt;
        this.airlineKorean = airlineKorean;
        this.city = city;
        this.airlineEnglish = airlineEnglish;
        this.airport = airport;
        this.internationalIdx = internationalIdx;   // 인천공항일 때 항공사 로고
        this.airlineHomepageUrl = airlineHomepageUrl;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof FlightSchedulesDTO that)) return false;
        return Objects.equals(airlineKorean, that.airlineKorean) && Objects.equals(airport, that.airport) && Objects.equals(internationalAirportDome, that.internationalAirportDome) && Objects.equals(internationalAirportInte, that.internationalAirportInte) && Objects.equals(internationalStdt, that.internationalStdt) && Objects.equals(internationalEddt, that.internationalEddt) && Objects.equals(internationalNum, that.internationalNum) && Objects.equals(internationalTime, that.internationalTime) && Objects.equals(internationalMon, that.internationalMon) && Objects.equals(internationalTue, that.internationalTue) && Objects.equals(internationalWed, that.internationalWed) && Objects.equals(internationalThu, that.internationalThu) && Objects.equals(internationalFri, that.internationalFri) && Objects.equals(internationalSat, that.internationalSat) && Objects.equals(internationalSun, that.internationalSun);
    }

    @Override
    public int hashCode() {
        return Objects.hash(airlineKorean, airport, internationalAirportDome, internationalAirportInte, internationalStdt, internationalEddt, internationalNum, internationalTime, internationalMon, internationalTue, internationalWed, internationalThu, internationalFri, internationalSat, internationalSun);
    }
}
