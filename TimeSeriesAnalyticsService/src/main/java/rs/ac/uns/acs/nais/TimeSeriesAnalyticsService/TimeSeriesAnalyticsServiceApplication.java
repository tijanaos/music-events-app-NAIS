package rs.ac.uns.acs.nais.TimeSeriesAnalyticsService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class TimeSeriesAnalyticsServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(TimeSeriesAnalyticsServiceApplication.class, args);
    }
}
