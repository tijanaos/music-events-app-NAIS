package rs.ac.uns.acs.nais.PerformerManagementService.dto.query;

public interface GenreNegotiationStats {
    String getGenre();
    Long getNegotiationCount();
    Double getAvgAgreedFee();
}
