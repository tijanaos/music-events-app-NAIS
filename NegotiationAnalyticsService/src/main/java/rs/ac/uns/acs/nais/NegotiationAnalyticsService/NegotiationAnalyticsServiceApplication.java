package rs.ac.uns.acs.nais.NegotiationAnalyticsService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableDiscoveryClient
@EnableCaching
public class NegotiationAnalyticsServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(NegotiationAnalyticsServiceApplication.class, args);
    }
}
