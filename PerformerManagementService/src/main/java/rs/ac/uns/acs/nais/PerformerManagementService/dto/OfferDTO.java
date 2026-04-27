package rs.ac.uns.acs.nais.PerformerManagementService.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import rs.ac.uns.acs.nais.PerformerManagementService.model.enums.OfferStatus;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OfferDTO {
    private Double price;
    private LocalDate eventDate;
    private String location;
    private Integer duration;
    private String additionalBenefits;
    private OfferStatus status;
    private String workflowTemplateId;
}
