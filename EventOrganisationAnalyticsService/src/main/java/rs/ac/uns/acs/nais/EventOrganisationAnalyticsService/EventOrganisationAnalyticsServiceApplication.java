package rs.ac.uns.acs.nais.EventOrganisationAnalyticsService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class EventOrganisationAnalyticsServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(EventOrganisationAnalyticsServiceApplication.class, args);
    }
}
