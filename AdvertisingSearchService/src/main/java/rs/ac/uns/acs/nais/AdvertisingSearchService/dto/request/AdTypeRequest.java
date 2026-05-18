package rs.ac.uns.acs.nais.AdvertisingSearchService.dto.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class AdTypeRequest {

    private Long id;

    @NotBlank
    private String name;

    @NotBlank
    private String description;

    @NotBlank
    private String contentType;

    @NotBlank
    private String category;

    @NotBlank
    private String targetChannel;

    @NotNull
    private Boolean isActive;

    @NotNull
    private Boolean requiresApproval;

    @NotNull
    private Integer averageDurationDays;

    private LocalDate createdAt;
}
