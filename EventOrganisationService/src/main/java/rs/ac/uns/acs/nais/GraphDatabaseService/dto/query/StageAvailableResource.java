package rs.ac.uns.acs.nais.GraphDatabaseService.dto.query;

public interface StageAvailableResource {
    String getStageId();
    String getStageName();
    String getStageType();
    Long getResourceCount();
    Long getTotalAvailable();
}
