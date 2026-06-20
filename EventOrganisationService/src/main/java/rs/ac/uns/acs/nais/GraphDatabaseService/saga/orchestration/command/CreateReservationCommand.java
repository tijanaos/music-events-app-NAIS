package rs.ac.uns.acs.nais.GraphDatabaseService.saga.orchestration.command;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import rs.ac.uns.acs.nais.GraphDatabaseService.dto.RequiresResourceDTO;
import rs.ac.uns.acs.nais.GraphDatabaseService.dto.ReservationDTO;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateReservationCommand {

    private String sagaId;
    private ReservationDTO reservation;
    private List<RequiresResourceDTO> requestedResources;
}