package rs.ac.uns.acs.nais.GraphDatabaseService.service;

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
}
