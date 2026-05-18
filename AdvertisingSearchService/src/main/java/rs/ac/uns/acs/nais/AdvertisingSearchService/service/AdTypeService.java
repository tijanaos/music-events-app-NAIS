package rs.ac.uns.acs.nais.AdvertisingSearchService.service;

import rs.ac.uns.acs.nais.AdvertisingSearchService.dto.request.AdTypeRequest;
import rs.ac.uns.acs.nais.AdvertisingSearchService.dto.response.AdTypeResponse;

import java.util.List;

public interface AdTypeService {
    List<AdTypeResponse> getAll();
    AdTypeResponse getById(Long id);
    AdTypeResponse create(AdTypeRequest request);
    AdTypeResponse update(Long id, AdTypeRequest request);
    void delete(Long id);
}
