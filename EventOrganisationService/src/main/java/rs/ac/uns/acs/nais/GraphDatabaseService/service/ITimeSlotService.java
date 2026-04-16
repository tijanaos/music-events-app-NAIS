package rs.ac.uns.acs.nais.GraphDatabaseService.service;

import rs.ac.uns.acs.nais.GraphDatabaseService.dto.TimeSlotDTO;
import rs.ac.uns.acs.nais.GraphDatabaseService.model.TimeSlot;
import rs.ac.uns.acs.nais.GraphDatabaseService.model.enums.SlotType;
import rs.ac.uns.acs.nais.GraphDatabaseService.model.enums.TimeSlotStatus;

import java.time.LocalDate;
import java.util.List;

public interface ITimeSlotService {

    List<TimeSlot> findAll();

    TimeSlot findById(String id);

    TimeSlot create(TimeSlotDTO dto);

    TimeSlot update(String id, TimeSlotDTO dto);

    void delete(String id);

    List<TimeSlot> findByStatus(TimeSlotStatus status);

    List<TimeSlot> findByDate(LocalDate date);

    List<TimeSlot> findBySlotType(SlotType slotType);

    List<TimeSlot> findFree();
}
