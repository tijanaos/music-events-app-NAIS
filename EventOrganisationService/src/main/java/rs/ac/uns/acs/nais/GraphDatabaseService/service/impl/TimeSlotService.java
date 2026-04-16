package rs.ac.uns.acs.nais.GraphDatabaseService.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import rs.ac.uns.acs.nais.GraphDatabaseService.dto.TimeSlotDTO;
import rs.ac.uns.acs.nais.GraphDatabaseService.mapper.TimeSlotMapper;
import rs.ac.uns.acs.nais.GraphDatabaseService.model.TimeSlot;
import rs.ac.uns.acs.nais.GraphDatabaseService.model.enums.SlotType;
import rs.ac.uns.acs.nais.GraphDatabaseService.model.enums.TimeSlotStatus;
import rs.ac.uns.acs.nais.GraphDatabaseService.repository.TimeSlotRepository;
import rs.ac.uns.acs.nais.GraphDatabaseService.service.ITimeSlotService;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TimeSlotService implements ITimeSlotService {

    private final TimeSlotRepository timeSlotRepository;
    private final TimeSlotMapper timeSlotMapper;

    @Override
    public List<TimeSlot> findAll() {
        return timeSlotRepository.findAll();
    }

    @Override
    public TimeSlot findById(String id) {
        return timeSlotRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "TimeSlot not found with id: " + id));
    }

    @Override
    public TimeSlot create(TimeSlotDTO dto) {
        return timeSlotRepository.save(timeSlotMapper.toEntity(dto));
    }

    @Override
    public TimeSlot update(String id, TimeSlotDTO dto) {
        TimeSlot existing = findById(id);
        timeSlotMapper.updateEntity(dto, existing);
        return timeSlotRepository.save(existing);
    }

    @Override
    public void delete(String id) {
        findById(id);
        timeSlotRepository.deleteById(id);
    }

    @Override
    public List<TimeSlot> findByStatus(TimeSlotStatus status) {
        return timeSlotRepository.findByStatus(status);
    }

    @Override
    public List<TimeSlot> findByDate(LocalDate date) {
        return timeSlotRepository.findByDate(date);
    }

    @Override
    public List<TimeSlot> findBySlotType(SlotType slotType) {
        return timeSlotRepository.findBySlotType(slotType);
    }

    @Override
    public List<TimeSlot> findFree() {
        return timeSlotRepository.findByStatus(TimeSlotStatus.FREE);
    }
}
