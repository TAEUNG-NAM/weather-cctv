package live.narcy.weather.entity;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class City {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String country;
    private String name;
    @Column(name ="weather_api")
    private String weatherApi;
}
