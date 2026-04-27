package rs.ac.uns.acs.nais.GraphDatabaseService.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import rs.ac.uns.acs.nais.GraphDatabaseService.model.enums.SharingType;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SharesResourceDTO {

    private String resourceId;
    private String targetStageId;
    private SharingType sharingType;
    private LocalDate dateFrom;
    private LocalDate dateTo;
}
