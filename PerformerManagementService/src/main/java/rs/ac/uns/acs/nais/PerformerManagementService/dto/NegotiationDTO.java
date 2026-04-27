package rs.ac.uns.acs.nais.PerformerManagementService.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NegotiationDTO {
    private String createdBy;
    private String offerId;
    private String performerId;
    private Double agreedFee;
    private String initialStateId;
}
