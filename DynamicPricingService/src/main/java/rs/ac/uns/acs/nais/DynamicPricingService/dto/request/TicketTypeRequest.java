package rs.ac.uns.acs.nais.DynamicPricingService.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TicketTypeRequest {

    private String name;
    private Integer maxAvailable;
    private Integer soldCount;
    private List<PriceScheduleRequest> schedules;
}
