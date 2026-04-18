package rs.ac.uns.acs.nais.GraphDatabaseService.config;

import org.springframework.data.neo4j.core.mapping.callback.BeforeBindCallback;
import org.springframework.stereotype.Component;
import rs.ac.uns.acs.nais.GraphDatabaseService.model.Performer;
import rs.ac.uns.acs.nais.GraphDatabaseService.model.Reservation;
import rs.ac.uns.acs.nais.GraphDatabaseService.model.Resource;
import rs.ac.uns.acs.nais.GraphDatabaseService.model.Stage;
import rs.ac.uns.acs.nais.GraphDatabaseService.model.TimeSlot;

import java.util.UUID;

@Component
public class UuidGenerationCallback
        implements BeforeBindCallback<Object> {

    @Override
    public Object onBeforeBind(Object entity) {
        if (entity instanceof Performer p && p.getId() == null) {
            p.setId(UUID.randomUUID().toString());
        } else if (entity instanceof Stage s && s.getId() == null) {
            s.setId(UUID.randomUUID().toString());
        } else if (entity instanceof Resource r && r.getId() == null) {
            r.setId(UUID.randomUUID().toString());
        } else if (entity instanceof TimeSlot t && t.getId() == null) {
            t.setId(UUID.randomUUID().toString());
        } else if (entity instanceof Reservation r && r.getId() == null) {
            r.setId(UUID.randomUUID().toString());
        }
        return entity;
    }
}
