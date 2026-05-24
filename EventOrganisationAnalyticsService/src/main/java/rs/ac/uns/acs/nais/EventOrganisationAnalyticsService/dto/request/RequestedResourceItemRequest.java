package rs.ac.uns.acs.nais.EventOrganisationAnalyticsService.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestedResourceItemRequest {

    @NotBlank
    private String resourceName;

    @NotBlank
    private String resourceType;

    @NotNull
    @Min(1)
    private Integer requestedQuantity;

    @NotNull
    private Boolean existsInSystem;

    private String resourceStatus;

    private String rejectionReason;
}
