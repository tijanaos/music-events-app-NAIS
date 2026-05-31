package rs.ac.uns.acs.nais.NegotiationAnalyticsService.repository;

import rs.ac.uns.acs.nais.NegotiationAnalyticsService.model.OfferLifecycleEvent;

import java.util.List;
import java.util.Map;

public interface OfferEventRepository {
    Boolean save(OfferLifecycleEvent record);
    Boolean saveBatch(List<OfferLifecycleEvent> records);
    Boolean deleteByOfferId(String offerId);
    List<OfferLifecycleEvent> findByOfferId(String offerId);
    List<Map<String, Object>> avgOfferPriceByEventTypeAndLocation();
}
