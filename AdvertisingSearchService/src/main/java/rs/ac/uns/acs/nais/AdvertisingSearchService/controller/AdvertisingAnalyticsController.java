package rs.ac.uns.acs.nais.AdvertisingSearchService.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import rs.ac.uns.acs.nais.AdvertisingSearchService.dto.response.AdPhaseSearchQueryResponse;
import rs.ac.uns.acs.nais.AdvertisingSearchService.dto.response.AdTypeSearchQueryResponse;
import rs.ac.uns.acs.nais.AdvertisingSearchService.dto.response.ApprovalSummaryQueryResponse;
import rs.ac.uns.acs.nais.AdvertisingSearchService.service.AdvertisingAnalyticsService;

import java.util.List;

@RestController
@RequestMapping("/api/advertising-queries")
@RequiredArgsConstructor
public class AdvertisingAnalyticsController {

    private final AdvertisingAnalyticsService advertisingAnalyticsService;

    @Operation(
            summary = "Pretraga aktivnih vrsta oglasa",
            description = "Pretražuje indeks vrsta oglasa po nazivu i opisu, zadržava samo aktivne tipove iz zadate kategorije i content type-a, a zatim vraća rezultate i pregled grupisan po ciljnom kanalu oglašavanja."
    )
    @GetMapping("/active-ad-types")
    public ResponseEntity<AdTypeSearchQueryResponse> searchActiveAdTypes(
            @Parameter(description = "Tekst koji se traži kroz polja name i description u indeksu ad-types.")
            @RequestParam(defaultValue = "video promocija") String text,
            @Parameter(description = "Kategorija oglasa koja mora tačno da se poklopi, na primer festival.")
            @RequestParam(defaultValue = "festival") String category,
            @Parameter(description = "Tip sadržaja oglasa koji mora tačno da se poklopi, na primer video.")
            @RequestParam(defaultValue = "video") String contentType) {
        return ResponseEntity.ok(advertisingAnalyticsService.searchActiveAdTypes(text, category, contentType));
    }

    @Operation(
            summary = "Pronalaženje faza sa notifikacijama",
            description = "Najpre pronalazi vrste oglasa po nazivu, pa zatim traži njihove aktivne faze koje šalju email notifikacije, nisu finalne i traju najmanje zadati broj sati. Rezultat se dodatno grupiše po odgovornoj ulozi."
    )
    @GetMapping("/notification-phases")
    public ResponseEntity<AdPhaseSearchQueryResponse> findNotificationPhases(
            @Parameter(description = "Naziv vrste oglasa preko koga se pronalaze povezane workflow faze.")
            @RequestParam(defaultValue = "Video oglas 1") String adTypeName,
            @Parameter(description = "Minimalno očekivano trajanje faze u satima.")
            @RequestParam(defaultValue = "12") int minimumDurationHours) {
        return ResponseEntity.ok(advertisingAnalyticsService.findNotificationPhases(adTypeName, minimumDurationHours));
    }

    @Operation(
            summary = "Tekstualna pretraga workflow faza",
            description = "Pretražuje workflow faze po poljima phase_name i description. Po potrebi može da vrati samo aktivne faze, a u odgovoru daje i grupisanje po odgovornoj ulozi."
    )
    @GetMapping("/phase-text-search")
    public ResponseEntity<AdPhaseSearchQueryResponse> searchPhaseWorkflowText(
            @Parameter(description = "Tekst koji se traži kroz naziv i opis workflow faze.")
            @RequestParam(defaultValue = "provera odobrenje validacija") String text,
            @Parameter(description = "Ako je true, vraćaju se samo aktivne faze.")
            @RequestParam(defaultValue = "true") boolean activeOnly) {
        return ResponseEntity.ok(advertisingAnalyticsService.searchPhaseWorkflowText(text, activeOnly));
    }

    @Operation(
            summary = "Pregled oglasa koji zahtevaju odobrenje",
            description = "Vraća aktivne vrste oglasa koje zahtevaju odobrenje, pripadaju nekoj od prosleđenih kategorija i imaju prosečno trajanje u zadatom rasponu dana. Odgovor sadrži i grupisanje po kategoriji."
    )
    @GetMapping("/approval-summary")
    public ResponseEntity<ApprovalSummaryQueryResponse> findApprovalHeavyAdTypes(
            @Parameter(description = "Lista kategorija nad kojima se radi filter, na primer festival,sponsor,performer.")
            @RequestParam(defaultValue = "festival,sponsor,performer") List<String> categories,
            @Parameter(description = "Minimalan prosečan broj dana trajanja oglasa.")
            @RequestParam(defaultValue = "7") int minDurationDays,
            @Parameter(description = "Maksimalan prosečan broj dana trajanja oglasa.")
            @RequestParam(defaultValue = "30") int maxDurationDays) {
        return ResponseEntity.ok(advertisingAnalyticsService.findApprovalHeavyAdTypes(categories, minDurationDays, maxDurationDays));
    }
}
