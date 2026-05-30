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
import java.util.List;

@Document(indexName = "reservation-requests")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ReservationRequestDocument {

    @Id
    private String id;

    @Field(name = "request_status", type = FieldType.Keyword)
    private String requestStatus;

    @Field(name = "sent_date", type = FieldType.Date, format = DateFormat.date)
    private LocalDate sentDate;

    @Field(name = "updated_date", type = FieldType.Date, format = DateFormat.date)
    private LocalDate updatedDate;

    @Field(name = "note", type = FieldType.Text)
    private String note;

    @Field(name = "stage_id", type = FieldType.Keyword)
    private String stageId;

    @Field(name = "stage_name", type = FieldType.Keyword)
    private String stageName;

    @Field(name = "stage_type", type = FieldType.Keyword)
    private String stageType;

    @Field(name = "stage_capacity", type = FieldType.Integer)
    private Integer stageCapacity;

    @Field(name = "performer_id", type = FieldType.Keyword)
    private String performerId;

    @Field(name = "performer_first_name", type = FieldType.Text)
    private String performerFirstName;

    @Field(name = "performer_last_name", type = FieldType.Text)
    private String performerLastName;

    @Field(name = "genre", type = FieldType.Keyword)
    private String genre;

    @Field(name = "popularity", type = FieldType.Double)
    private Double popularity;

    @Field(name = "performance_date", type = FieldType.Date, format = DateFormat.date)
    private LocalDate performanceDate;

    @Field(name = "start_time", type = FieldType.Integer)
    private Integer startTime;

    @Field(name = "end_time", type = FieldType.Integer)
    private Integer endTime;

    @Field(name = "requested_resources", type = FieldType.Nested)
    private List<RequestedResourceItem> requestedResources;

    @Field(name = "has_tasks", type = FieldType.Boolean)
    private Boolean hasTasks;

    @Field(name = "task_count", type = FieldType.Integer)
    private Integer taskCount;

    @Field(name = "performance_details", type = FieldType.Text)
    private String performanceDetails;
}
