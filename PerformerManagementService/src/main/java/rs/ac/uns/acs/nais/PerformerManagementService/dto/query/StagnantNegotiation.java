package rs.ac.uns.acs.nais.PerformerManagementService.dto.query;

public interface StagnantNegotiation {
    String getNegotiationId();
    String getManager();
    String getCurrentState();
    Long getDaysInState();
    Long getAllowedDays();
    Long getDaysOverdue();
}
