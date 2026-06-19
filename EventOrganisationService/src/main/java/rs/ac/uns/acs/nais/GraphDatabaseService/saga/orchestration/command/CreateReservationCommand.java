package rs.ac.uns.acs.nais.GraphDatabaseService.saga.orchestration.command;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import rs.ac.uns.acs.nais.GraphDatabaseService.dto.RequiresResourceDTO;
import rs.ac.uns.acs.nais.GraphDatabaseService.dto.ReservationDTO;

import java.util.List;

/**
 * Komanda koju SagaOrchestrator salje da bi Neo4j servis kreirao novu rezervaciju.
 * Nosi ReservationDTO (bina, termin, izvodjac) i listu trazenih resursa koje
 * treba povezati sa rezervacijom (REQUIRES_RESOURCE grane).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateReservationCommand {

    private String sagaId;
    private ReservationDTO reservation;
    private List<RequiresResourceDTO> requestedResources;
}