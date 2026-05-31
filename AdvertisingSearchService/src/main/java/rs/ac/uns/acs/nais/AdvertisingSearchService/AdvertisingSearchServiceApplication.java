package rs.ac.uns.acs.nais.AdvertisingSearchService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class AdvertisingSearchServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AdvertisingSearchServiceApplication.class, args);
    }
}
