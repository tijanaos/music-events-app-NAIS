package rs.ac.uns.acs.nais.NegotiationAnalyticsService.repository;

import rs.ac.uns.acs.nais.NegotiationAnalyticsService.model.NegotiationStateHistory;

import java.util.List;
import java.util.Map;

public interface NegotiationStateRepository {
    Boolean save(NegotiationStateHistory record);
    Boolean saveBatch(List<NegotiationStateHistory> records);
    Boolean deleteByNegotiationId(String negotiationId);
    List<NegotiationStateHistory> findByNegotiationId(String negotiationId);
    List<Map<String, Object>> avgDurationPerStateByTemplate(String templateName);
    List<Map<String, Object>> monthlyNegotiationSuccessTrend();
}
