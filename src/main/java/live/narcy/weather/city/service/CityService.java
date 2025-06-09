package live.narcy.weather.city.service;

import jakarta.persistence.EntityNotFoundException;
import live.narcy.weather.city.entity.Area;
import live.narcy.weather.city.entity.City;
import live.narcy.weather.city.repository.AreaRepository;
import live.narcy.weather.city.repository.CityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnailator;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;
import net.coobird.thumbnailator.name.Rename;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CityService {

    private final CityRepository cityRepository;
    private final AreaRepository areaRepository;
    @Value("${file.upload.root.path}")
    private String THUMBNAIL_PATH;

    public List<City> getCityList(String countryName) {
        return cityRepository.findByCountryAndDelYn(countryName, "n");
    }

    public List<City> getCityAllList(String countryName) {
        return cityRepository.findAllByCountry(countryName);
    }

    public City getCity(String cityName) {
        return cityRepository.findByName(cityName);
    }

    public City getCityByKor(String cityName) {
        return cityRepository.findByKorName(cityName);
    }

    public List<Area> getAreaList(City city) {
        return areaRepository.findByCityAndDelYn(city, "n");
    }


    /**
     * 신규 도시 추가 및 썸네일 생성
     * @param param
     * @param thumbnail
     * @return
     */
    @Transactional
    public City saveCity(Map<String, String> param, MultipartFile thumbnail) {
        try {
            File dir = new File(THUMBNAIL_PATH);     // 디렉토리 경로가 없을 시 생성(#2)

            if (!dir.exists()) {
                if (!dir.mkdirs()){
                    throw new IOException("Failed to create directory");
                }
            }

            // 원본 이미지 생성
            File ogImg = new File(THUMBNAIL_PATH + param.get("cityName") + ".png");
            thumbnail.transferTo(ogImg);
            
            // 썸네일 생성
            Thumbnails.of(ogImg)
                    .outputQuality(1)
                    .forceSize(472, 378)    // width:472px, height:378px
                    .toFiles(dir, Rename.NO_CHANGE);

            log.info("thumbnailPath = {}", THUMBNAIL_PATH);

            City newCity = City.builder()
                    .name(param.get("cityName"))
                    .korName(param.get("cityKorName"))
                    .country(param.get("country"))
                    .thumbnail(THUMBNAIL_PATH + param.get("cityName") + ".png")
                    .delYn("n")
                    .build();

            return cityRepository.save(newCity);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 도시 수정 및 썸네일 변경
     * @param param
     * @param thumbnail
     * @return
     */
    @Transactional
    public City updateCity(Map<String, String> param, MultipartFile thumbnail) {
        City targetCity = cityRepository.findById(Long.valueOf(param.get("cityId"))).orElseThrow(
                () -> new EntityNotFoundException("해당 도시를 찾을 수 없습니다.")
        );

        try {
            // 원본 이미지 생성
            File ogImg = new File(THUMBNAIL_PATH + param.get("cityName") + ".png");
            thumbnail.transferTo(ogImg);

            // 썸네일 생성
            Thumbnails.of(ogImg)
                    .outputQuality(1)
                    .forceSize(472, 378)    // width:472px, height:378px
                    .toFiles(new File(THUMBNAIL_PATH), Rename.NO_CHANGE);

            City newCity = City.builder()
                    .id(Long.valueOf(param.get("cityId")))
                    .country(param.get("country"))
                    .name(param.get("cityName"))
                    .korName(param.get("cityKorName"))
                    .thumbnail(THUMBNAIL_PATH + param.get("cityName") + ".png")
                    .delYn(param.get("delYn"))
                    .build();

            City updatedCity = targetCity.patch(newCity);

            cityRepository.save(updatedCity);

            return updatedCity;
            
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Transactional
    public City deleteCity(String targetCountry, String targetCityId) {
        City targetCity = cityRepository.findById(Long.valueOf(targetCityId)).orElseThrow(
                () -> new EntityNotFoundException("해당 도시를 찾을 수 없습니다.")
        );

        cityRepository.deleteById(Long.valueOf(targetCityId));

        return targetCity;
    }
}
