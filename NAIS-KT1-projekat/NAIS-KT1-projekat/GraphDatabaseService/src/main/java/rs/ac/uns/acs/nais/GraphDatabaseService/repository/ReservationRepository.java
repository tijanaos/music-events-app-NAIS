package rs.ac.uns.acs.nais.GraphDatabaseService.repository;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;
import rs.ac.uns.acs.nais.GraphDatabaseService.model.Reservation;
import rs.ac.uns.acs.nais.GraphDatabaseService.model.enums.ReservationStatus;

import java.util.List;

@Repository
public interface ReservationRepository extends Neo4jRepository<Reservation, String> {

    List<Reservation> findByStatus(ReservationStatus status);

    List<Reservation> findByCreatedBy(String createdBy);
}
