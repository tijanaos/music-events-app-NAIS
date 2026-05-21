package rs.ac.uns.acs.nais.EventOrganisationAnalyticsService.service;

import rs.ac.uns.acs.nais.EventOrganisationAnalyticsService.dto.request.ResourceUsageDto;
import rs.ac.uns.acs.nais.EventOrganisationAnalyticsService.dto.response.ResourceUsageResponse;

import java.time.LocalDate;
import java.util.List;

public interface ResourceUsageService {

    ResourceUsageResponse create(ResourceUsageDto dto);

    ResourceUsageResponse findById(String id);

    List<ResourceUsageResponse> findAll();

    List<ResourceUsageResponse> findByBinaId(String binaId);

    List<ResourceUsageResponse> findByResursId(String resursId);

    List<ResourceUsageResponse> findByTipResursa(String tipResursa);

    List<ResourceUsageResponse> findByPeriod(LocalDate from, LocalDate to);

    List<ResourceUsageResponse> findPozajmice();

    List<ResourceUsageResponse> findByRezervacijaId(String rezervacijaId);

    ResourceUsageResponse update(String id, ResourceUsageDto dto);

    void delete(String id);
}
