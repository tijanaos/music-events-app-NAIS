package rs.ac.uns.acs.nais.GraphDatabaseService.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import rs.ac.uns.acs.nais.GraphDatabaseService.dto.TimeSlotDTO;
import rs.ac.uns.acs.nais.GraphDatabaseService.model.TimeSlot;

@Mapper(componentModel = "spring")
public interface TimeSlotMapper {

    TimeSlot toEntity(TimeSlotDTO dto);

    TimeSlotDTO toDTO(TimeSlot timeSlot);

    void updateEntity(TimeSlotDTO dto, @MappingTarget TimeSlot timeSlot);
}
