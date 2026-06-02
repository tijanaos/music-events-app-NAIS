package rs.ac.uns.acs.nais.AdvertisingSearchService.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class AdTypeResponse {
    private Long id;
    private String name;
    private String description;
    private String contentType;
    private String category;
    private String targetChannel;
    private Boolean isActive;
    private Boolean requiresApproval;
    private Integer averageDurationDays;
    private LocalDate createdAt;
}
