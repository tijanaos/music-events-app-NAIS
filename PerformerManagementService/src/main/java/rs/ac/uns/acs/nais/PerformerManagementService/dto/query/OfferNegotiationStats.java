package rs.ac.uns.acs.nais.PerformerManagementService.dto.query;

public interface OfferNegotiationStats {
    String getOfferId();
    Double getPrice();
    String getOfferStatus();
    Long getTotalNegotiations();
    Long getConcluded();
    Long getFailed();
}
