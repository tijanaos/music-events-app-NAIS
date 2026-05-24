package rs.ac.uns.acs.nais.EventOrganisationAnalyticsService.model;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class RequestedResourceItem {

    @Field(name = "resource_name", type = FieldType.Text)
    private String resourceName;

    @Field(name = "resource_type", type = FieldType.Keyword)
    private String resourceType;

    @Field(name = "requested_quantity", type = FieldType.Integer)
    private Integer requestedQuantity;

    @Field(name = "exists_in_system", type = FieldType.Boolean)
    private Boolean existsInSystem;

    @Field(name = "resource_status", type = FieldType.Keyword)
    private String resourceStatus;

    @Field(name = "rejection_reason", type = FieldType.Text)
    private String rejectionReason;
}
