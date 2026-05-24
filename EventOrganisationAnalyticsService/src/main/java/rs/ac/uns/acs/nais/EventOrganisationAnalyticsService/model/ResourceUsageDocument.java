package rs.ac.uns.acs.nais.EventOrganisationAnalyticsService.model;

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

@Document(indexName = "resource-usage")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ResourceUsageDocument {

    @Id
    private String id;

    // Resurs
    @Field(name = "resource_id", type = FieldType.Keyword)
    private String resourceId;

    @Field(name = "resource_name", type = FieldType.Text)
    private String resourceName;

    @Field(name = "resource_type", type = FieldType.Keyword)
    private String resourceType;

    @Field(name = "portable", type = FieldType.Boolean)
    private Boolean portable;

    @Field(name = "allocated_quantity", type = FieldType.Integer)
    private Integer allocatedQuantity;

    // Bina na kojoj se koristi resurs
    @Field(name = "stage_id", type = FieldType.Keyword)
    private String stageId;

    @Field(name = "stage_name", type = FieldType.Text)
    private String stageName;

    @Field(name = "stage_type", type = FieldType.Keyword)
    private String stageType;

    // Termin koriscenja
    @Field(name = "time_slot_id", type = FieldType.Keyword)
    private String timeSlotId;

    @Field(name = "date", type = FieldType.Date, format = DateFormat.date)
    private LocalDate date;

    @Field(name = "start_time", type = FieldType.Integer)
    private Integer startTime;

    @Field(name = "end_time", type = FieldType.Integer)
    private Integer endTime;

    // Pozajmica sa druge bine
    @Field(name = "borrowed_from_stage", type = FieldType.Boolean)
    private Boolean borrowedFromStage;

    @Field(name = "borrowing_stage_name", type = FieldType.Keyword)
    private String borrowingStageName;

    // Veza sa zahtevom
    @Field(name = "reservation_id", type = FieldType.Keyword)
    private String reservationId;
}
