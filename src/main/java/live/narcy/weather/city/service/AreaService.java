package live.narcy.weather.city.service;

import jakarta.persistence.EntityNotFoundException;
import live.narcy.weather.city.dto.AreaDTO;
import live.narcy.weather.city.entity.Area;
import live.narcy.weather.city.entity.City;
import live.narcy.weather.city.repository.AreaRepository;
import live.narcy.weather.city.repository.CityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AreaService {
    private final AreaRepository areaRepository;
    private final CityRepository cityRepository;

    public List<Area> getCctvData(String cityName) {
        City city = cityRepository.findByName(cityName);

        return areaRepository.findByCity(city);
    }

    @Transactional
    @Validated
    public List<AreaDTO> updateCctvData(List<AreaDTO> dtoList) {

        List<AreaDTO> updatedList = new ArrayList<>();

        for(AreaDTO area : dtoList) {

            Area newArea;
            if(area.getId() == null) {
                log.info("area = {}", area);
                City city = cityRepository.findByName(area.getCity());
                if(city == null) {
                    throw new EntityNotFoundException("해당 도시를 찾을 수 없습니다.");
                } else {
                    newArea = area.toArea(city);
                }
            } else {
                // Area 조회
                Area targetArea = areaRepository.findById(area.getId()).orElseThrow(
                        () -> new EntityNotFoundException("해당 구역이 존재하지 않습니다.")
                );

                // Area 데이터 갱신 및 저장
                newArea = targetArea.patch(area);
            }

            areaRepository.save(newArea);
            updatedList.add(Area.toDTO(newArea));
        }

        return updatedList;
    }

    @Transactional
    public void deleteCctvData(Long cctvId) {

        areaRepository.findById(cctvId).orElseThrow(
                () -> new EntityNotFoundException("해당 구역이 존재하지 않습니다!")
        );

        areaRepository.deleteById(cctvId);
    }
}
