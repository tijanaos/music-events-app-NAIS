package rs.ac.uns.acs.nais.EventOrganisationAnalyticsService.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;
import rs.ac.uns.acs.nais.EventOrganisationAnalyticsService.model.ResourceUsageDocument;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ResourceUsageRepository extends ElasticsearchRepository<ResourceUsageDocument, String> {

    List<ResourceUsageDocument> findByBinaId(String binaId);

    List<ResourceUsageDocument> findByResursId(String resursId);

    List<ResourceUsageDocument> findByTipResursa(String tipResursa);

    List<ResourceUsageDocument> findByDatumBetween(LocalDate from, LocalDate to);

    List<ResourceUsageDocument> findByPozajmljenoSaBineTrue();

    List<ResourceUsageDocument> findByRezervacijaId(String rezervacijaId);
}
