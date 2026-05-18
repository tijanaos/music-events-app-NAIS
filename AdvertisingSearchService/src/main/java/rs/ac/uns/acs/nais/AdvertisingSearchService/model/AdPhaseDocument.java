package rs.ac.uns.acs.nais.AdvertisingSearchService.model;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDate;

@Document(indexName = "ad-phases")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class AdPhaseDocument {

    @Id
    private Long id;

    @Field(name = "ad_type_id", type = FieldType.Long)
    private Long adTypeId;

    @Field(name = "phase_name", type = FieldType.Text)
    private String phaseName;

    @Field(type = FieldType.Text)
    private String description;

    @Field(name = "phase_order", type = FieldType.Integer)
    private Integer phaseOrder;

    @Field(name = "responsible_role", type = FieldType.Keyword)
    private String responsibleRole;

    @Field(name = "requires_email_notification", type = FieldType.Boolean)
    private Boolean requiresEmailNotification;

    @Field(name = "is_final_phase", type = FieldType.Boolean)
    private Boolean isFinalPhase;

    @Field(name = "is_active", type = FieldType.Boolean)
    private Boolean isActive;

    @Field(name = "expected_duration_hours", type = FieldType.Integer)
    private Integer expectedDurationHours;

    @Field(name = "created_at", type = FieldType.Date, format = DateFormat.date)
    private LocalDate createdAt;
}
