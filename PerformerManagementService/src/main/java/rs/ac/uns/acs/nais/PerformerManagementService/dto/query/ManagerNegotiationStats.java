package rs.ac.uns.acs.nais.PerformerManagementService.dto.query;

public interface ManagerNegotiationStats {
    String getManager();
    Long getTotalNegotiations();
    Long getConcluded();
    Double getSuccessRate();
}
