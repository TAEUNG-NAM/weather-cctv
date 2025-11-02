package live.narcy.weather.flightSchedule.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import live.narcy.weather.flightSchedule.dto.AvailableAirlineDto;
import live.narcy.weather.flightSchedule.dto.AvailableDestinationsDto;
import live.narcy.weather.flightSchedule.dto.FlightSchedulesDto;
import live.narcy.weather.flightSchedule.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ScheduleApiController {

    private final ScheduleService scheduleService;

    /**
     * 도착지 & 항공사 조회
     * @param param
     * @return 출발지에서 갈 수 있는 도착지&항공사 목록
     * @throws JsonProcessingException
     */
    @PostMapping("/api/search/destinations")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getAvailableDestinations(@RequestBody String param) throws JsonProcessingException {
        log.info(param);

        List<AvailableDestinationsDto> availableDestinationsList = scheduleService.getDestinations(param);
        List<AvailableAirlineDto> availableAirlineList = scheduleService.getAvailableAirlines(param);

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("availableDestinationsList", availableDestinationsList);
        resultMap.put("availableAirlineList", availableAirlineList);

        return ResponseEntity.ok(resultMap);
    }


    /**
     * 운항 스케줄 조회
     * @param param
     * @return
     * @throws JsonProcessingException
     */
    @PostMapping("/api/search/flight-schedules")
    @ResponseBody
    public ResponseEntity<List<FlightSchedulesDto>> getFlightSchedules(@RequestBody String param) throws JsonProcessingException {
        log.info(param);

        List<FlightSchedulesDto> flightSchedulesList = scheduleService.getSchedules(param);

        return ResponseEntity.ok(flightSchedulesList);
    }
}
