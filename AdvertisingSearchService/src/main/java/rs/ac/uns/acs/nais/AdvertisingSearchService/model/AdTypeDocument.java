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

@Document(indexName = "ad-types")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class AdTypeDocument {

    @Id
    private Long id;

    @Field(type = FieldType.Text)
    private String name;

    @Field(type = FieldType.Text)
    private String description;

    @Field(name = "content_type", type = FieldType.Keyword)
    private String contentType;

    @Field(type = FieldType.Keyword)
    private String category;

    @Field(name = "target_channel", type = FieldType.Keyword)
    private String targetChannel;

    @Field(name = "is_active", type = FieldType.Boolean)
    private Boolean isActive;

    @Field(name = "requires_approval", type = FieldType.Boolean)
    private Boolean requiresApproval;

    @Field(name = "average_duration_days", type = FieldType.Integer)
    private Integer averageDurationDays;

    @Field(name = "created_at", type = FieldType.Date, format = DateFormat.date)
    private LocalDate createdAt;
}
