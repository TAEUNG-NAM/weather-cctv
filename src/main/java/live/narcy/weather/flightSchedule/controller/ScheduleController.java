package live.narcy.weather.flightSchedule.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import live.narcy.weather.flightSchedule.dto.AvailableAirlineDTO;
import live.narcy.weather.flightSchedule.dto.AvailableDestinationsDTO;
import live.narcy.weather.flightSchedule.dto.FlightSchedulesDTO;
import live.narcy.weather.flightSchedule.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ScheduleController {

    private final ScheduleService scheduleService;
    private final ObjectMapper objectMapper;

    @GetMapping("/flight-schedule")
    public String schedulePage() {

        return "contents/searchFlight";
    }

    /**
     * 도착지 & 항공사 조회
     * @param param
     * @return 출발지에서 갈 수 있는 도착지&항공사 목록
     * @throws JsonProcessingException
     */
    @PostMapping("/api/search/destinations")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getAvailableDestinations(@RequestBody String param) throws JsonProcessingException {
        log.info("도착지&항공사 조회 = {}", param);

        List<AvailableDestinationsDTO> availableDestinationsList = scheduleService.getDestinations(param);
        List<AvailableAirlineDTO> availableAirlineList = scheduleService.getAvailableAirlines(param);

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
    public ResponseEntity<List<FlightSchedulesDTO>> getFlightSchedules(@RequestBody String param) throws JsonProcessingException {
        log.info("운항스케줄 조회 = {}", param);

        List<FlightSchedulesDTO> flightSchedulesList = scheduleService.getSchedules(param);

        return ResponseEntity.ok(flightSchedulesList);
    }
}
