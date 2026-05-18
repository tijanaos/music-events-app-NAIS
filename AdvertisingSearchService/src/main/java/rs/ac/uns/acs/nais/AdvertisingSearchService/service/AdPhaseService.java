package rs.ac.uns.acs.nais.AdvertisingSearchService.service;

import rs.ac.uns.acs.nais.AdvertisingSearchService.dto.request.AdPhaseRequest;
import rs.ac.uns.acs.nais.AdvertisingSearchService.dto.response.AdPhaseResponse;

import java.util.List;

public interface AdPhaseService {
    List<AdPhaseResponse> getAll();
    AdPhaseResponse getById(Long id);
    AdPhaseResponse create(AdPhaseRequest request);
    AdPhaseResponse update(Long id, AdPhaseRequest request);
    void delete(Long id);
}
