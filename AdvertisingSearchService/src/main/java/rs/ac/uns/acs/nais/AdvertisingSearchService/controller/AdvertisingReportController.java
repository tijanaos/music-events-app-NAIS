package rs.ac.uns.acs.nais.AdvertisingSearchService.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import rs.ac.uns.acs.nais.AdvertisingSearchService.service.AdvertisingReportService;

import java.io.IOException;

@RestController
@RequestMapping("/api/advertising-reports")
@RequiredArgsConstructor
public class AdvertisingReportController {

    private final AdvertisingReportService advertisingReportService;

    @GetMapping(value = "/export-pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> exportPdf(
            @RequestParam(defaultValue = "festival lineup reveal") String text,
            @RequestParam(defaultValue = "festival") String category,
            @RequestParam(defaultValue = "video") String contentType,
            @RequestParam(defaultValue = "1") Integer kampanjaId,
            @RequestParam(defaultValue = "headline artist reveal main stage crowd and weekend festival energy") String semanticQuery,
            @RequestParam(defaultValue = "10") Integer topK) {
        try {
            byte[] pdfContents = advertisingReportService.export(text, category, contentType, kampanjaId, semanticQuery, topK);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "advertising-report.pdf");

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(pdfContents);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
