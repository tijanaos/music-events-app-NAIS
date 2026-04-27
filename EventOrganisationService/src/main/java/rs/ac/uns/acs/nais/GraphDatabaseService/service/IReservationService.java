package rs.ac.uns.acs.nais.GraphDatabaseService.service;

import rs.ac.uns.acs.nais.GraphDatabaseService.dto.ForPerformerUpdateDTO;
import rs.ac.uns.acs.nais.GraphDatabaseService.dto.OccupiesSlotUpdateDTO;
import rs.ac.uns.acs.nais.GraphDatabaseService.dto.OnStageUpdateDTO;
import rs.ac.uns.acs.nais.GraphDatabaseService.dto.RequiresResourceDTO;
import rs.ac.uns.acs.nais.GraphDatabaseService.dto.ReservationDTO;
import rs.ac.uns.acs.nais.GraphDatabaseService.model.Reservation;
import rs.ac.uns.acs.nais.GraphDatabaseService.model.enums.ReservationStatus;

import java.util.List;

public interface IReservationService {

    List<Reservation> findAll();

    Reservation findById(String id);

    Reservation create(ReservationDTO dto);

    Reservation update(String id, ReservationDTO dto);

    void delete(String id);

    List<Reservation> findByStatus(ReservationStatus status);

    List<Reservation> findByCreatedBy(String createdBy);

    Reservation updateStatus(String id, ReservationStatus status);

    Reservation addResource(String reservationId, RequiresResourceDTO dto);

    Reservation updateResource(String reservationId, String resourceId, RequiresResourceDTO dto);

    Reservation removeResource(String reservationId, String resourceId);

    Reservation updateStageRelation(String reservationId, OnStageUpdateDTO dto);

    Reservation updateSlotRelation(String reservationId, OccupiesSlotUpdateDTO dto);

    Reservation updatePerformerRelation(String reservationId, ForPerformerUpdateDTO dto);
}
