package live.narcy.weather.city.service;

import com.azure.core.util.Context;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.models.BlobHttpHeaders;
import com.azure.storage.blob.options.BlobParallelUploadOptions;
import jakarta.persistence.EntityNotFoundException;
import live.narcy.weather.city.dto.CityDto;
import live.narcy.weather.city.entity.City;
import live.narcy.weather.city.repository.CityRepository;
import live.narcy.weather.config.exception.CustomException;
import live.narcy.weather.config.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.name.Rename;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.WritableResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CityService {

    private final CityRepository cityRepository;
    @Value("${file.upload.root.path}")
    private String THUMBNAIL_PATH;
    // Azure Blob Container Client 주입
    private final BlobContainerClient blobContainerClient;

    /**
     * 특정 국가의 모든 도시 조회(숨겨진 도시 제외)
     * @param countryName
     * @return
     */
    @Transactional(readOnly = true)
    public List<CityDto> getCities(String countryName) {
        return cityRepository.findCitiesByCountryName(countryName);
    }

    /**
     * 특정 국가의 모든 도시(City) 조회(숨겨진 도시 포함)
     * @param countryName
     * @return
     */
    @Transactional(readOnly = true)
    public List<CityDto> getAllCities(String countryName) {
        return cityRepository.findAllCitiesByCountryName(countryName);
    }

    /**
     * 특정 도시 조회
     * @param cityName
     * @return
     */
    @Transactional(readOnly = true)
    public City getCity(String cityName) {
        return cityRepository.findByName(cityName).orElseThrow(
                () -> new CustomException(ErrorCode.CITY_NOT_FOUND)
        );
    }

    /**
     * 도시명으로 도시 검색(영문&한글)
     * @param keyword
     * @return
     */
    @Transactional(readOnly = true)
    public Optional<City> getCityByKeyword(String keyword) {
        Optional<City> findedCity = cityRepository.findByName(keyword);
        if(findedCity.isPresent()) {
            return findedCity;
        }

        return cityRepository.findByKorName(keyword);
    }

    /**
     * 신규 도시 추가 및 썸네일 생성
     * @param param
     * @param thumbnail
     * @return
     */
    @Transactional
    public CityDto saveCity(CityDto param, MultipartFile thumbnail) {
        String engName = param.getEngName();
        if(cityRepository.existsByName(engName)) {
            throw new CustomException(ErrorCode.CITY_ALREADY_EXISTS);
        }

        // 최종 저장될 썸네일 이름
        String fileName = engName + ".jpg";
        try (
                InputStream inputStream = thumbnail.getInputStream();
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream()
        ) {
            // 썸네일 생성
            Thumbnails.of(inputStream)
                    .outputQuality(0.95)
                    .outputFormat("jpg")
                    .forceSize(472, 378)
                    .toOutputStream(outputStream);

            // 메모리에 저장된 썸네일 데이터를 byte 배열로 변환
            byte[] processedImageBytes = outputStream.toByteArray();

            // Azure Blob Client 가져오기
            BlobClient blobClient = blobContainerClient.getBlobClient(fileName);

            // byte 배열을 Azure Blob Storage에 업로드
            try (InputStream processedInputStream = new ByteArrayInputStream(processedImageBytes)) {

                // 파일 업로드 (true: 덮어쓰기 허용)
                blobClient.upload(processedInputStream, processedImageBytes.length, true);

                // 업로드된 이미지의 Content-Type을 "image/jpeg"로 설정
                BlobHttpHeaders headers = new BlobHttpHeaders().setContentType("image/jpeg");
                blobClient.setHttpHeaders(headers);

                log.info("썸네일 업로드 완료. URL: {}", blobClient.getBlobUrl());
            }

            City newCity = City.builder()
                    .name(engName)
                    .korName(param.getKorName())
                    .country(param.getCountry())
                    .thumbnail(blobClient.getBlobUrl())
                    .delYn("n")
                    .build();

            return CityDto.from(cityRepository.save(newCity));

        } catch (IOException e) {
            log.error("썸네일 저장 중 에러 발생 = {}", String.valueOf(e));
            throw new CustomException(ErrorCode.FAIL_CREATE_CITY);
        } catch (Exception e) {
            // Azure SDK 관련 예외 처리
            log.error("Azure Blob Storage 업로드 중 오류 발생", e);
            throw new CustomException(ErrorCode.FAIL_CREATE_CITY);
        }
    }

    /**
     * 도시 수정 및 썸네일 변경
     * @param param
     * @param thumbnail
     * @return
     */
    @Transactional
    public CityDto updateCity(CityDto param, MultipartFile thumbnail) {
        City targetCity = cityRepository.findById(param.getId()).orElseThrow(
                () -> new CustomException(ErrorCode.CITY_NOT_FOUND)
        );

        // 썸네일 파일 이름
        String fileName = param.getEngName() + ".jpg";
        try (
                InputStream inputStream = thumbnail.getInputStream();
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream()
        ) {
            if(!thumbnail.isEmpty()) {

                // 새로운 썸네일 생성
                Thumbnails.of(inputStream)
                        .outputQuality(0.95)
                        .outputFormat("jpg")
                        .forceSize(472, 378)
                        .toOutputStream(outputStream);

                // 메모리에 저장된 썸네일 데이터를 byte 배열로 변환
                byte[] processedImageBytes = outputStream.toByteArray();

                // Azure Blob Client 가져오기
                BlobClient blobClient = blobContainerClient.getBlobClient(fileName);

                // byte 배열을 Azure Blob Storage에 업로드
                try (InputStream processedInputStream = new ByteArrayInputStream(processedImageBytes)) {

                    // 파일 업로드 (true: 덮어쓰기 허용)
                    blobClient.upload(processedInputStream, processedImageBytes.length, true);

                    // 업로드된 이미지의 Content-Type을 "image/jpeg"로 설정
                    BlobHttpHeaders headers = new BlobHttpHeaders().setContentType("image/jpeg");
                    blobClient.setHttpHeaders(headers);

                    log.info("썸네일 업데이트 완료. URL: {}", blobClient.getBlobUrl());
                }
            }

            targetCity.patch(param);
            return CityDto.from(targetCity);

        } catch (IOException e) {
            log.error("썸네일 저장 중 에러 발생 = {}", String.valueOf(e));
            throw new CustomException(ErrorCode.FAIL_CREATE_CITY);
        } catch (Exception e) {
            // Azure SDK 관련 예외 처리
            log.error("Azure Blob Storage 업로드 중 오류 발생", e);
            throw new CustomException(ErrorCode.FAIL_CREATE_CITY);
        }
    }

    /**
     * 도시 삭제
     * @param targetCountry
     * @param targetCityId
     */
    @Transactional
    public void deleteCity(String targetCountry, Long targetCityId) {
        cityRepository.findById(targetCityId).orElseThrow(
                () -> new EntityNotFoundException("해당 도시를 찾을 수 없습니다.")
        );

        cityRepository.deleteById(targetCityId);
    }


}
