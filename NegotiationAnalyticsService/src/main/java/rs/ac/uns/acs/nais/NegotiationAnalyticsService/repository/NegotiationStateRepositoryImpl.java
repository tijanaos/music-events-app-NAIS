package rs.ac.uns.acs.nais.NegotiationAnalyticsService.repository;

import com.influxdb.client.InfluxDBClient;
import org.springframework.stereotype.Repository;
import rs.ac.uns.acs.nais.NegotiationAnalyticsService.configuration.InfluxDBConnectionClass;
import rs.ac.uns.acs.nais.NegotiationAnalyticsService.model.NegotiationStateHistory;

import java.util.List;
import java.util.Map;

@Repository
public class NegotiationStateRepositoryImpl implements NegotiationStateRepository {

    private final InfluxDBConnectionClass inConn;

    public NegotiationStateRepositoryImpl(InfluxDBConnectionClass inConn) {
        this.inConn = inConn;
    }

    @Override
    public Boolean save(NegotiationStateHistory record) {
        InfluxDBClient client = inConn.buildConnection();
        Boolean result = inConn.saveNegotiationState(client, record);
        client.close();
        return result;
    }

    @Override
    public Boolean saveBatch(List<NegotiationStateHistory> records) {
        InfluxDBClient client = inConn.buildConnection();
        Boolean result = inConn.saveNegotiationStateBatch(client, records);
        client.close();
        return result;
    }

    @Override
    public Boolean deleteByNegotiationId(String negotiationId) {
        InfluxDBClient client = inConn.buildConnection();
        Boolean result = inConn.deleteNegotiationStateByNegotiationId(client, negotiationId);
        client.close();
        return result;
    }

    @Override
    public List<NegotiationStateHistory> findByNegotiationId(String negotiationId) {
        InfluxDBClient client = inConn.buildConnection();
        List<NegotiationStateHistory> result = inConn.findStatesByNegotiationId(client, negotiationId);
        client.close();
        return result;
    }

    @Override
    public List<Map<String, Object>> avgDurationPerStateByTemplate(String templateName) {
        InfluxDBClient client = inConn.buildConnection();
        List<Map<String, Object>> result = inConn.avgDurationPerStateByTemplate(client, templateName);
        client.close();
        return result;
    }

    @Override
    public List<Map<String, Object>> monthlyNegotiationSuccessTrend() {
        InfluxDBClient client = inConn.buildConnection();
        List<Map<String, Object>> result = inConn.monthlyNegotiationSuccessTrend(client);
        client.close();
        return result;
    }
}
