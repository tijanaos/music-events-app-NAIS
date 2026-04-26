package rs.ac.uns.acs.nais.PerformerManagementService.config;

import org.springframework.data.neo4j.core.mapping.callback.BeforeBindCallback;
import org.springframework.stereotype.Component;
import rs.ac.uns.acs.nais.PerformerManagementService.model.Performer;
import rs.ac.uns.acs.nais.PerformerManagementService.model.Offer;
import rs.ac.uns.acs.nais.PerformerManagementService.model.Negotiation;
import rs.ac.uns.acs.nais.PerformerManagementService.model.State;
import rs.ac.uns.acs.nais.PerformerManagementService.model.WorkflowTemplate;

import java.util.UUID;

@Component
public class UuidGenerationCallback implements BeforeBindCallback<Object> {

    @Override
    public Object onBeforeBind(Object entity) {
        if (entity instanceof Performer p && p.getId() == null) {
            p.setId(UUID.randomUUID().toString());
        } else if (entity instanceof Offer o && o.getId() == null) {
            o.setId(UUID.randomUUID().toString());
        } else if (entity instanceof Negotiation n && n.getId() == null) {
            n.setId(UUID.randomUUID().toString());
        } else if (entity instanceof State s && s.getId() == null) {
            s.setId(UUID.randomUUID().toString());
        } else if (entity instanceof WorkflowTemplate wt && wt.getId() == null) {
            wt.setId(UUID.randomUUID().toString());
        }
        return entity;
    }
}
