package rs.ac.uns.acs.nais.GraphDatabaseService.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import rs.ac.uns.acs.nais.GraphDatabaseService.dto.ReservationDTO;
import rs.ac.uns.acs.nais.GraphDatabaseService.model.*;
import rs.ac.uns.acs.nais.GraphDatabaseService.model.enums.ReservationStatus;
import rs.ac.uns.acs.nais.GraphDatabaseService.model.enums.TimeSlotStatus;
import rs.ac.uns.acs.nais.GraphDatabaseService.model.relationship.ForPerformer;
import rs.ac.uns.acs.nais.GraphDatabaseService.model.relationship.OccupiesSlot;
import rs.ac.uns.acs.nais.GraphDatabaseService.model.relationship.OnStage;
import rs.ac.uns.acs.nais.GraphDatabaseService.repository.*;
import rs.ac.uns.acs.nais.GraphDatabaseService.service.IReservationService;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationService implements IReservationService {

    private final ReservationRepository reservationRepository;
    private final StageRepository stageRepository;
    private final TimeSlotRepository timeSlotRepository;
    private final PerformerRepository performerRepository;

    @Override
    public List<Reservation> findAll() {
        return reservationRepository.findAll();
    }

    @Override
    public Reservation findById(String id) {
        return reservationRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reservation not found with id: " + id));
    }

    @Override
    public Reservation create(ReservationDTO dto) {
        Stage stage = stageRepository.findById(dto.getStageId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Stage not found with id: " + dto.getStageId()));

        TimeSlot timeSlot = timeSlotRepository.findById(dto.getTimeSlotId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "TimeSlot not found with id: " + dto.getTimeSlotId()));

        Performer performer = performerRepository.findById(dto.getPerformerId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Performer not found with id: " + dto.getPerformerId()));

        OnStage onStage = OnStage.builder()
                .confirmed(dto.getStageConfirmed())
                .stage(stage)
                .build();

        OccupiesSlot occupiesSlot = OccupiesSlot.builder()
                .reservationDate(dto.getSlotReservationDate())
                .systemSuggestion(dto.getSystemSuggestion())
                .timeSlot(timeSlot)
                .build();

        ForPerformer forPerformer = ForPerformer.builder()
                .managerUsername(dto.getManagerUsername())
                .agreedFee(dto.getAgreedFee())
                .performer(performer)
                .build();

        Reservation reservation = Reservation.builder()
                .status(dto.getStatus() != null ? dto.getStatus() : ReservationStatus.CREATED)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .note(dto.getNote())
                .performanceDetails(dto.getPerformanceDetails())
                .createdBy(dto.getCreatedBy())
                .stage(onStage)
                .timeSlot(occupiesSlot)
                .performer(forPerformer)
                .build();

        timeSlot.setStatus(TimeSlotStatus.RESERVED);
        timeSlotRepository.save(timeSlot);

        return reservationRepository.save(reservation);
    }

    @Override
    public Reservation update(String id, ReservationDTO dto) {
        Reservation existing = findById(id);
        existing.setStatus(dto.getStatus());
        existing.setNote(dto.getNote());
        existing.setPerformanceDetails(dto.getPerformanceDetails());
        existing.setUpdatedAt(LocalDateTime.now());
        return reservationRepository.save(existing);
    }

    @Override
    public void delete(String id) {
        Reservation reservation = findById(id);
        if (reservation.getTimeSlot() != null) {
            TimeSlot timeSlot = reservation.getTimeSlot().getTimeSlot();
            timeSlot.setStatus(TimeSlotStatus.FREE);
            timeSlotRepository.save(timeSlot);
        }
        reservationRepository.deleteById(id);
    }

    @Override
    public List<Reservation> findByStatus(ReservationStatus status) {
        return reservationRepository.findByStatus(status);
    }

    @Override
    public List<Reservation> findByCreatedBy(String createdBy) {
        return reservationRepository.findByCreatedBy(createdBy);
    }

    @Override
    public Reservation updateStatus(String id, ReservationStatus status) {
        Reservation existing = findById(id);
        existing.setStatus(status);
        existing.setUpdatedAt(LocalDateTime.now());
        return reservationRepository.save(existing);
    }
}
