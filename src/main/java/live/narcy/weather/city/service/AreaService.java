package live.narcy.weather.city.service;

import jakarta.persistence.EntityNotFoundException;
import live.narcy.weather.city.dto.AreaDTO;
import live.narcy.weather.city.entity.Area;
import live.narcy.weather.city.entity.City;
import live.narcy.weather.city.repository.AreaRepository;
import live.narcy.weather.city.repository.CityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AreaService {
    private final AreaRepository areaRepository;
    private final CityRepository cityRepository;

    public List<Area> getCctvData(String cityName) {
        City city = cityRepository.findByName(cityName);

        return areaRepository.findByCity(city);
    }

    @Transactional
    public List<AreaDTO> updateCctvData(List<AreaDTO> dtoList) {

        List<AreaDTO> updatedList = new ArrayList<>();

        for(AreaDTO area : dtoList) {
            // Area 조회
            Area targetArea = areaRepository.findById(area.getId()).orElseThrow(
                () -> new EntityNotFoundException("해당 지역이 존재하지 않습니다.")
            );

            // Area 데이터 갱신 및 저장
            Area newArea = targetArea.patch(area);
            areaRepository.save(newArea);
            updatedList.add(AreaDTO.toDTO(newArea));
        }

        return updatedList;
    }
}
