package rs.ac.uns.acs.nais.DynamicPricingService.service;

import rs.ac.uns.acs.nais.DynamicPricingService.dto.request.PromoCodeRequest;
import rs.ac.uns.acs.nais.DynamicPricingService.dto.response.PromoCodeResponse;

import java.util.List;

public interface PromoCodeService {

    List<PromoCodeResponse> getAll();
    PromoCodeResponse getById(String id);
    PromoCodeResponse create(PromoCodeRequest request);
    PromoCodeResponse update(String id, PromoCodeRequest request);
    void delete(String id);
}
