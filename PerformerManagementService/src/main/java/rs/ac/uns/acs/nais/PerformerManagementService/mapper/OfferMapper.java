package rs.ac.uns.acs.nais.PerformerManagementService.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import rs.ac.uns.acs.nais.PerformerManagementService.dto.OfferDTO;
import rs.ac.uns.acs.nais.PerformerManagementService.model.Offer;

@Mapper(componentModel = "spring")
public interface OfferMapper {

    @Mapping(target = "workflowTemplate", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "publishedAt", ignore = true)
    Offer toEntity(OfferDTO dto);

    OfferDTO toDTO(Offer offer);

    @Mapping(target = "workflowTemplate", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "publishedAt", ignore = true)
    void updateEntity(OfferDTO dto, @MappingTarget Offer offer);
}
