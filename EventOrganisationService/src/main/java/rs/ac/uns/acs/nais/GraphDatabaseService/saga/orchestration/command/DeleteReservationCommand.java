package rs.ac.uns.acs.nais.GraphDatabaseService.saga.orchestration.command;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Kompenzaciona komanda -- nalaze Neo4j servisu da obrise prethodno kreiranu
 * rezervaciju (i oslobodi pripadajuci TimeSlot) zato sto upis u Elasticsearch
 * (korak 2 sage) nije uspeo.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeleteReservationCommand {

    private String sagaId;
    private String reservationId;
}