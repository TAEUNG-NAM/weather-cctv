package live.narcy.weather.city.service;

import live.narcy.weather.city.dto.AreaDto;
import live.narcy.weather.city.entity.Area;
import live.narcy.weather.city.entity.City;
import live.narcy.weather.city.repository.AreaRepository;
import live.narcy.weather.city.repository.CityRepository;
import live.narcy.weather.config.exception.CustomException;
import live.narcy.weather.config.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class AreaService {
    private final AreaRepository areaRepository;
    private final CityRepository cityRepository;

    /**
     * 구역(CCTV) 조회
     * @param cityName
     * @return
     */
    @Transactional(readOnly = true)
    public List<AreaDto> getCctvData(String cityName) {
        City city = cityRepository.findByName(cityName).orElseThrow(
                () -> new CustomException(ErrorCode.CITY_NOT_FOUND));

        List<AreaDto> dtos = new ArrayList<>();
        List<Area> areas = areaRepository.findByCity(city);
        for(Area area : areas) {
            dtos.add(AreaDto.from(area));
        }

        return dtos;
    }

    /**
     * 특정 도시의 모든 구역(CCTV) 조회
     * @param cityName
     * @return
     */
    @Transactional(readOnly = true)
    public List<AreaDto> getAreaList(String cityName) {
        List<AreaDto> dtos = new ArrayList<>();

        List<Area> areas = areaRepository.findByCityName(cityName);

        for(Area area : areas) {
            dtos.add(AreaDto.from(area));
        }

        return dtos;
    }

    /**
     * 구역(CCTV) 생성 및 수정
     * @param dtoList
     */
    @Transactional
    @Validated
    public void updateCctvData(List<AreaDto> dtoList) {
        List<Area> areaList = new ArrayList<>();
        List<AreaDto> toCreate = new ArrayList<>();
        List<AreaDto> toUpdate = new ArrayList<>();

        // 신규, 수정 구분 작업
        for(AreaDto dto : dtoList) {
            if(dto.getId() == null) {
                toCreate.add(dto);      // 신규 구역
            } else {
                toUpdate.add(dto);      // 수정 구역
            }
        }

        /* 수정 시작 */
        // 수정할 구역 ID 추출
        List<Long> updateIds = new ArrayList<>();
        for(AreaDto dto : toUpdate) {
            updateIds.add(dto.getId());
        }

        // 수정할 구역 모두 가져오기
        List<Area> existingAreas = areaRepository.findAllById(updateIds);
        Map<Long, Area> existingAreasMap = new HashMap<>();
        for(Area area : existingAreas) {
            existingAreasMap.put(area.getId(), area);
        }

        // DB에서 가져온 구역이랑 비교 후 수정 처리
        for(AreaDto dto : toUpdate) {
            Area targetArea = existingAreasMap.get(dto.getId());
            if(targetArea != null) {
                targetArea.patch(dto);
            } else {
               throw new CustomException(ErrorCode.AREA_NOT_FOUND);
            }
        }   /* 수정 종료 */

        /* 신규 생성 시작 */
        // 구역 정보를 통해 도시 검증
        for(AreaDto dto : toCreate) {
            City city = cityRepository.findByName(dto.getCity()).orElseThrow(
                    () -> new CustomException(ErrorCode.CITY_NOT_FOUND)
            );
            Area area = Area.from(dto, city);
            city.addArea(area);
            areaList.add(area);
        }
        // 신규 구역 일괄 생성
        areaRepository.saveAll(areaList);   /* 신규 생성 종료 */
    }

    /**
     * 구역(CCTV) 삭제
     * @param cctvId
     */
    @Transactional
    public void deleteCctvData(Long cctvId) {
        areaRepository.findById(cctvId).orElseThrow(
                () -> new CustomException(ErrorCode.AREA_NOT_FOUND)
        );
        areaRepository.deleteById(cctvId);
    }
}
