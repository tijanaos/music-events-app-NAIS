package rs.ac.uns.acs.nais.DynamicPricingService.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TicketTypeResponse {

    private String ticketTypeId;
    private String name;
    private Integer maxAvailable;
    private Integer soldCount;
    private List<PriceScheduleResponse> schedules;
}
