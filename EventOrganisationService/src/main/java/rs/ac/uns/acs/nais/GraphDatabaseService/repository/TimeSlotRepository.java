package rs.ac.uns.acs.nais.GraphDatabaseService.repository;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;
import rs.ac.uns.acs.nais.GraphDatabaseService.model.TimeSlot;
import rs.ac.uns.acs.nais.GraphDatabaseService.model.enums.SlotType;
import rs.ac.uns.acs.nais.GraphDatabaseService.model.enums.TimeSlotStatus;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TimeSlotRepository extends Neo4jRepository<TimeSlot, String> {

    List<TimeSlot> findByStatus(TimeSlotStatus status);

    List<TimeSlot> findBySlotType(SlotType slotType);

    List<TimeSlot> findByDate(LocalDate date);

    List<TimeSlot> findByDateBetween(LocalDate from, LocalDate to);
}
