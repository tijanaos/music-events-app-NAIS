package rs.ac.uns.acs.nais.PerformerManagementService.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import rs.ac.uns.acs.nais.PerformerManagementService.dto.OfferDTO;
import rs.ac.uns.acs.nais.PerformerManagementService.mapper.OfferMapper;
import rs.ac.uns.acs.nais.PerformerManagementService.model.Offer;
import rs.ac.uns.acs.nais.PerformerManagementService.model.WorkflowTemplate;
import rs.ac.uns.acs.nais.PerformerManagementService.model.enums.OfferStatus;
import rs.ac.uns.acs.nais.PerformerManagementService.model.relationship.BasedOn;
import rs.ac.uns.acs.nais.PerformerManagementService.repository.OfferRepository;
import rs.ac.uns.acs.nais.PerformerManagementService.repository.WorkflowTemplateRepository;
import rs.ac.uns.acs.nais.PerformerManagementService.service.IOfferService;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OfferService implements IOfferService {

    private final OfferRepository offerRepository;
    private final WorkflowTemplateRepository workflowTemplateRepository;
    private final OfferMapper offerMapper;

    @Override
    public List<Offer> findAll() {
        return offerRepository.findAll();
    }

    @Override
    public Offer findById(String id) {
        return offerRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Offer not found with id: " + id));
    }

    @Override
    public Offer create(OfferDTO dto) {
        Offer offer = offerMapper.toEntity(dto);
        offer.setStatus(OfferStatus.CREATED);
        offer.setCreatedAt(LocalDateTime.now());

        if (dto.getWorkflowTemplateId() != null) {
            WorkflowTemplate template = workflowTemplateRepository.findById(dto.getWorkflowTemplateId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "WorkflowTemplate not found with id: " + dto.getWorkflowTemplateId()));
            offer.setWorkflowTemplate(BasedOn.builder()
                    .assignedAt(LocalDateTime.now())
                    .workflowTemplate(template)
                    .build());
        }
        return offerRepository.save(offer);
    }

    @Override
    public Offer update(String id, OfferDTO dto) {
        Offer existing = findById(id);
        if (existing.getStatus() != OfferStatus.CREATED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only offers in CREATED status can be updated");
        }
        offerMapper.updateEntity(dto, existing);
        return offerRepository.save(existing);
    }

    @Override
    public Offer publish(String id) {
        Offer existing = findById(id);
        if (existing.getStatus() != OfferStatus.CREATED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only offers in CREATED status can be published");
        }
        existing.setStatus(OfferStatus.PUBLISHED);
        existing.setPublishedAt(LocalDateTime.now());
        return offerRepository.save(existing);
    }

    @Override
    public Offer archive(String id) {
        Offer existing = findById(id);
        existing.setStatus(OfferStatus.ARCHIVED);
        return offerRepository.save(existing);
    }

    @Override
    public List<Offer> findByStatus(OfferStatus status) {
        return offerRepository.findByStatus(status);
    }
}
