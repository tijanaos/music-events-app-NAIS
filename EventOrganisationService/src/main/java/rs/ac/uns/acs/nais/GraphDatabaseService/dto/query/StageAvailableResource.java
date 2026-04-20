package rs.ac.uns.acs.nais.GraphDatabaseService.dto.query;

public interface StageAvailableResource {
    String getStageId();
    String getStageName();
    String getResourceId();
    String getResourceName();
    Long getAvailableQuantity();
}
