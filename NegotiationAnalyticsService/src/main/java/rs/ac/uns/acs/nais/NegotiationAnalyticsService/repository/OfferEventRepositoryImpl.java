package rs.ac.uns.acs.nais.NegotiationAnalyticsService.repository;

import com.influxdb.client.InfluxDBClient;
import org.springframework.stereotype.Repository;
import rs.ac.uns.acs.nais.NegotiationAnalyticsService.configuration.InfluxDBConnectionClass;
import rs.ac.uns.acs.nais.NegotiationAnalyticsService.model.OfferLifecycleEvent;

import java.util.List;
import java.util.Map;

@Repository
public class OfferEventRepositoryImpl implements OfferEventRepository {

    private final InfluxDBConnectionClass inConn;

    public OfferEventRepositoryImpl(InfluxDBConnectionClass inConn) {
        this.inConn = inConn;
    }

    @Override
    public Boolean save(OfferLifecycleEvent record) {
        InfluxDBClient client = inConn.buildConnection();
        Boolean result = inConn.saveOfferEvent(client, record);
        client.close();
        return result;
    }

    @Override
    public Boolean saveBatch(List<OfferLifecycleEvent> records) {
        InfluxDBClient client = inConn.buildConnection();
        Boolean result = inConn.saveOfferEventBatch(client, records);
        client.close();
        return result;
    }

    @Override
    public Boolean deleteByOfferId(String offerId) {
        InfluxDBClient client = inConn.buildConnection();
        Boolean result = inConn.deleteOfferEventsByOfferId(client, offerId);
        client.close();
        return result;
    }

    @Override
    public List<OfferLifecycleEvent> findByOfferId(String offerId) {
        InfluxDBClient client = inConn.buildConnection();
        List<OfferLifecycleEvent> result = inConn.findEventsByOfferId(client, offerId);
        client.close();
        return result;
    }

    @Override
    public List<Map<String, Object>> avgOfferPriceByEventTypeAndLocation() {
        InfluxDBClient client = inConn.buildConnection();
        List<Map<String, Object>> result = inConn.avgOfferPriceByEventTypeAndLocation(client);
        client.close();
        return result;
    }
}
