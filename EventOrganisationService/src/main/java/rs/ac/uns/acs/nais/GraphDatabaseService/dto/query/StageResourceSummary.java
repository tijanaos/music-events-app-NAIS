package rs.ac.uns.acs.nais.GraphDatabaseService.dto.query;

public interface StageResourceSummary {
    String getStageId();
    String getStageName();
    String getStageType();
    Long getTotalResources();
    Long getTotalAvailableQuantity();
}
