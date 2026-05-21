package rs.ac.uns.acs.nais.EventOrganisationAnalyticsService.service;

import rs.ac.uns.acs.nais.EventOrganisationAnalyticsService.dto.request.ReservationRequestDto;
import rs.ac.uns.acs.nais.EventOrganisationAnalyticsService.dto.response.ReservationRequestResponse;

import java.util.List;

public interface ReservationRequestService {

    ReservationRequestResponse create(ReservationRequestDto dto);

    ReservationRequestResponse findById(String id);

    List<ReservationRequestResponse> findAll();

    List<ReservationRequestResponse> findByBinaId(String binaId);

    List<ReservationRequestResponse> findByStatus(String status);

    List<ReservationRequestResponse> findByIzvodjacId(String izvodjacId);

    List<ReservationRequestResponse> findWithTasks();

    ReservationRequestResponse update(String id, ReservationRequestDto dto);

    void delete(String id);
}
