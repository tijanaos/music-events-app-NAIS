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
import java.time.LocalTime;
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

    // Zahtev za rezervaciju
    @Field(name = "status_zahteva", type = FieldType.Keyword)
    private String statusZahteva;

    @Field(name = "datum_slanja", type = FieldType.Date, format = DateFormat.date)
    private LocalDate datumSlanja;

    @Field(name = "datum_azuriranja", type = FieldType.Date, format = DateFormat.date)
    private LocalDate datumAzuriranja;

    @Field(name = "napomena", type = FieldType.Text)
    private String napomena;

    // Denormalizovani podaci o bini
    @Field(name = "bina_id", type = FieldType.Keyword)
    private String binaId;

    @Field(name = "naziv_bine", type = FieldType.Text)
    private String nazivBine;

    @Field(name = "tip_bine", type = FieldType.Keyword)
    private String tipBine;

    @Field(name = "kapacitet_bine", type = FieldType.Integer)
    private Integer kapacitetBine;

    // Denormalizovani podaci o izvodjaci
    @Field(name = "izvodjac_id", type = FieldType.Keyword)
    private String izvodjacId;

    @Field(name = "ime_izvodjaca", type = FieldType.Text)
    private String imeIzvodjaca;

    @Field(name = "prezime_izvodjaca", type = FieldType.Text)
    private String prezimeIzvodjaca;

    @Field(name = "zanr", type = FieldType.Keyword)
    private String zanr;

    @Field(name = "popularnost", type = FieldType.Double)
    private Double popularnost;

    // Denormalizovani podaci o terminu
    @Field(name = "datum_nastupa", type = FieldType.Date, format = DateFormat.date)
    private LocalDate datumNastupa;

    @Field(name = "vreme_pocetka", type = FieldType.Integer)
    private Integer vremePocetka;

    @Field(name = "vreme_kraja", type = FieldType.Integer)
    private Integer vremeKraja;

    // Resursi iz zahteva
    @Field(name = "zahtevani_resursi", type = FieldType.Nested)
    private List<ZahtevaniResursItem> zahtevanihResursa;

    @Field(name = "ima_taskove", type = FieldType.Boolean)
    private Boolean imaTaskove;

    @Field(name = "broj_taskova", type = FieldType.Integer)
    private Integer brojTaskova;

    @Field(name = "detalji_nastupa", type = FieldType.Text)
    private String detaljiNastupa;
}
