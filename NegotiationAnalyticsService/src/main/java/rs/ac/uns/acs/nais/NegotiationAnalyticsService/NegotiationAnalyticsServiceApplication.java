package rs.ac.uns.acs.nais.NegotiationAnalyticsService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class NegotiationAnalyticsServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(NegotiationAnalyticsServiceApplication.class, args);
    }
}
