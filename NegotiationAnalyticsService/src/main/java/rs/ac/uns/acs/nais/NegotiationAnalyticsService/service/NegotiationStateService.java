package rs.ac.uns.acs.nais.NegotiationAnalyticsService.service;

import org.springframework.stereotype.Service;
import rs.ac.uns.acs.nais.NegotiationAnalyticsService.model.NegotiationStateHistory;
import rs.ac.uns.acs.nais.NegotiationAnalyticsService.repository.NegotiationStateRepositoryImpl;

import java.util.List;
import java.util.Map;

@Service
public class NegotiationStateService {

    private final NegotiationStateRepositoryImpl repository;

    public NegotiationStateService(NegotiationStateRepositoryImpl repository) {
        this.repository = repository;
    }

    public boolean save(NegotiationStateHistory record) {
        return repository.save(record);
    }

    public boolean saveBatch(List<NegotiationStateHistory> records) {
        return repository.saveBatch(records);
    }

    public boolean deleteByNegotiationId(String negotiationId) {
        return repository.deleteByNegotiationId(negotiationId);
    }

    public List<NegotiationStateHistory> findByNegotiationId(String negotiationId) {
        return repository.findByNegotiationId(negotiationId);
    }

    // Upit 1: identifikacija uskih grla
    public List<Map<String, Object>> avgDurationPerStateByTemplate(String templateName) {
        return repository.avgDurationPerStateByTemplate(templateName);
    }

    // Upit 2: mesecni trendovi uspesno zakljuceni nasuprot propalih ugovora
    public List<Map<String, Object>> monthlyNegotiationSuccessTrend() {
        return repository.monthlyNegotiationSuccessTrend();
    }
}
