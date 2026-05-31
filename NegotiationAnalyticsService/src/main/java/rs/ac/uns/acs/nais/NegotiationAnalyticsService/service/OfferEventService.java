package rs.ac.uns.acs.nais.NegotiationAnalyticsService.service;

import org.springframework.stereotype.Service;
import rs.ac.uns.acs.nais.NegotiationAnalyticsService.model.OfferLifecycleEvent;
import rs.ac.uns.acs.nais.NegotiationAnalyticsService.repository.OfferEventRepositoryImpl;
import java.util.Map;
import java.util.List;

@Service
public class OfferEventService {

    private final OfferEventRepositoryImpl repository;

    public OfferEventService(OfferEventRepositoryImpl repository) {
        this.repository = repository;
    }

    public boolean save(OfferLifecycleEvent record) {
        return repository.save(record);
    }

    public boolean saveBatch(List<OfferLifecycleEvent> records) {
        return repository.saveBatch(records);
    }

    public boolean deleteByOfferId(String offerId) {
        return repository.deleteByOfferId(offerId);
    }

    public List<OfferLifecycleEvent> findByOfferId(String offerId) {
        return repository.findByOfferId(offerId);
    }

    // Upit 3: prosecna cena ponude po tipu dogadjaja i lokaciji
    public List<Map<String, Object>> avgOfferPriceByEventTypeAndLocation() {
        return repository.avgOfferPriceByEventTypeAndLocation();
    }
}
