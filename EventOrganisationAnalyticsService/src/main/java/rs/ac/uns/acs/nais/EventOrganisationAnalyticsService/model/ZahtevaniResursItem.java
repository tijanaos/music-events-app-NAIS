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
public class ZahtevaniResursItem {

    @Field(name = "naziv_resursa", type = FieldType.Text)
    private String nazivResursa;

    @Field(name = "tip_resursa", type = FieldType.Keyword)
    private String tipResursa;

    @Field(name = "zahtevana_kolicina", type = FieldType.Integer)
    private Integer zahtevanrKolicina;

    @Field(name = "postoji_u_sistemu", type = FieldType.Boolean)
    private Boolean postojiUSistemu;

    @Field(name = "status_resursa", type = FieldType.Keyword)
    private String statusResursa;

    @Field(name = "razlog_odbijanja", type = FieldType.Text)
    private String razlogOdbijanja;
}
