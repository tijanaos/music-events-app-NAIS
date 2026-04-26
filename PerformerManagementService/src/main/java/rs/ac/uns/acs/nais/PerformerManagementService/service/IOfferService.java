package rs.ac.uns.acs.nais.PerformerManagementService.service;

import rs.ac.uns.acs.nais.PerformerManagementService.dto.OfferDTO;
import rs.ac.uns.acs.nais.PerformerManagementService.model.Offer;
import rs.ac.uns.acs.nais.PerformerManagementService.model.enums.OfferStatus;

import java.util.List;

public interface IOfferService {
    List<Offer> findAll();
    Offer findById(String id);
    Offer create(OfferDTO dto);
    Offer update(String id, OfferDTO dto);
    Offer publish(String id);
    Offer archive(String id);
    List<Offer> findByStatus(OfferStatus status);
}
