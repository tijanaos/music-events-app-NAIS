package rs.ac.uns.acs.nais.DynamicPricingService.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.ArrayList;
import java.util.List;

@Node("TicketType")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TicketType {

    @Id
    private String id;
    private String name;
    private Integer maxAvailable;
    private Integer soldCount;

    @Relationship(type = "HAS_SCHEDULE", direction = Relationship.Direction.OUTGOING)
    private List<PriceSchedule> schedules = new ArrayList<>();
}
