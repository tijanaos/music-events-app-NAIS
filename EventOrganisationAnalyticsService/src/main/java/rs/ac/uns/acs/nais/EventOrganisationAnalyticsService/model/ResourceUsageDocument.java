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
    @Field(name = "resurs_id", type = FieldType.Keyword)
    private String resursId;

    @Field(name = "naziv_resursa", type = FieldType.Text)
    private String nazivResursa;

    @Field(name = "tip_resursa", type = FieldType.Keyword)
    private String tipResursa;

    @Field(name = "prenosiv", type = FieldType.Boolean)
    private Boolean prenosiv;

    @Field(name = "dodeljena_kolicina", type = FieldType.Integer)
    private Integer dodeljenaKolicina;

    // Bina na kojoj se koristi resurs
    @Field(name = "bina_id", type = FieldType.Keyword)
    private String binaId;

    @Field(name = "naziv_bine", type = FieldType.Text)
    private String nazivBine;

    @Field(name = "tip_bine", type = FieldType.Keyword)
    private String tipBine;

    // Termin koriscenja
    @Field(name = "termin_id", type = FieldType.Keyword)
    private String terminId;

    @Field(name = "datum", type = FieldType.Date, format = DateFormat.date)
    private LocalDate datum;

    @Field(name = "vreme_pocetka", type = FieldType.Integer)
    private Integer vremePocetka;

    @Field(name = "vreme_kraja", type = FieldType.Integer)
    private Integer vremeKraja;

    // Pozajmica sa druge bine
    @Field(name = "pozajmljeno_sa_bine", type = FieldType.Boolean)
    private Boolean pozajmljenoSaBine;

    @Field(name = "naziv_bine_pozajmice", type = FieldType.Keyword)
    private String nazivBinePozajmice;

    // Veza sa zahtevom
    @Field(name = "rezervacija_id", type = FieldType.Keyword)
    private String rezervacijaId;
}
