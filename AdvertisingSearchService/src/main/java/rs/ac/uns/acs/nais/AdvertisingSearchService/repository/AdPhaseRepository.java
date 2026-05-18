package rs.ac.uns.acs.nais.AdvertisingSearchService.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import rs.ac.uns.acs.nais.AdvertisingSearchService.model.AdPhaseDocument;

public interface AdPhaseRepository extends ElasticsearchRepository<AdPhaseDocument, Long> {
}
