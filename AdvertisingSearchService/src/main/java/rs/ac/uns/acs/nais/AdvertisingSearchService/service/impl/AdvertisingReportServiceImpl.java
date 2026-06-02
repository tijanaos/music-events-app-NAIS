package rs.ac.uns.acs.nais.AdvertisingSearchService.service.impl;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import rs.ac.uns.acs.nais.AdvertisingSearchService.dto.request.VectorSemanticSearchRequest;
import rs.ac.uns.acs.nais.AdvertisingSearchService.dto.response.AdTypeResponse;
import rs.ac.uns.acs.nais.AdvertisingSearchService.dto.response.AdTypeSearchQueryResponse;
import rs.ac.uns.acs.nais.AdvertisingSearchService.dto.response.AggregationBucketResponse;
import rs.ac.uns.acs.nais.AdvertisingSearchService.dto.response.VectorOglasResponse;
import rs.ac.uns.acs.nais.AdvertisingSearchService.dto.response.VectorOglasSearchResult;
import rs.ac.uns.acs.nais.AdvertisingSearchService.service.AdvertisingAnalyticsService;
import rs.ac.uns.acs.nais.AdvertisingSearchService.service.AdvertisingReportService;

import javax.imageio.ImageIO;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdvertisingReportServiceImpl implements AdvertisingReportService {

    private static final Color HEADER_BACKGROUND = new Color(196, 227, 255);
    private static final Color BAR_COLOR = new Color(56, 132, 255);

    private final AdvertisingAnalyticsService advertisingAnalyticsService;
    private final RestTemplate restTemplate;

    @Value("${vector-service.base-url}")
    private String vectorServiceBaseUrl;

    @Override
    public byte[] export(String text, String category, String contentType, Integer kampanjaId, String semanticQuery, Integer topK)
            throws IOException {
        ReportData reportData = collectReportData(text, category, contentType, kampanjaId, semanticQuery, topK);
        List<AdTypeResponse> displayedAdTypes = safeAdTypeResults(reportData.activeAdTypes());

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4.rotate(), 36, 36, 36, 36);
        PdfWriter.getInstance(document, byteArrayOutputStream);
        document.open();

        addTitle(document);
        addFiltersSummary(document, text, category, contentType, kampanjaId, semanticQuery, topK);

        addSectionHeading(document, "Prosta sekcija 1: Aktivne vrste oglasa iz Elasticsearch baze");
        addParagraph(document, buildAdTypesSummary(reportData.activeAdTypes().getTotalHits(), displayedAdTypes.size(), text));
        addAdTypesTable(document, displayedAdTypes);

        addSectionHeading(document, "Prosta sekcija 2: Aktivni oglasi kampanje iz vektorske baze");
        addParagraph(document, String.format(Locale.ROOT,
                "Za kampanju %d i kategoriju '%s' vraceno je %d oglasa.",
                kampanjaId, category, reportData.activeAds().size()));
        addActiveAdsTable(document, reportData.activeAds());

        addSectionHeading(document, "Slozena sekcija: Semanticki preporuceni oglasi za aktivne tipove");
        addParagraph(document,
                "Ova sekcija najpre koristi Elasticsearch da odredi aktivne tipove oglasa i ciljne kanale, " +
                        "a zatim vrsi semanticku pretragu u Milvus bazi i zadrzava samo oglase kompatibilne sa tim pravilima.");
        addComplexResultsTable(document, reportData.recommendedAds(), reportData.allowedChannelsSummary());
        addSectionHeading(document, "Grafikon: Semanticka relevantnost preporucenih oglasa");
        addSemanticScoreChart(document, reportData.recommendedAds());

        addSectionHeading(document, "Grafikon: Prosecno trajanje prikazanih vrsta oglasa");
        addDurationChart(document, displayedAdTypes);

        addSectionHeading(document, "Zakljucak");
        addParagraph(document, buildConclusion(reportData, category, contentType, kampanjaId));

        document.close();
        return byteArrayOutputStream.toByteArray();
    }

    private ReportData collectReportData(String text, String category, String contentType, Integer kampanjaId, String semanticQuery, Integer topK) {
        AdTypeSearchQueryResponse activeAdTypes = advertisingAnalyticsService.searchActiveAdTypes(text, category, contentType);

        String vectorTip = mapContentTypeToVectorTip(contentType);
        List<VectorOglasResponse> activeAds = fetchActiveAds(category, kampanjaId, vectorTip);
        List<VectorOglasSearchResult> semanticResults = fetchSemanticResults(category, semanticQuery, topK, vectorTip);

        List<Long> activeTypeIds = safeAdTypeResults(activeAdTypes).stream()
                .map(AdTypeResponse::getId)
                .filter(Objects::nonNull)
                .toList();

        List<VectorOglasSearchResult> recommendedAds = semanticResults.stream()
                .filter(result -> kampanjaId == null || kampanjaId.equals(result.getKampanjaId()))
                .filter(result -> result.getAdTypeId() == null || result.getAdTypeId() == 0 || activeTypeIds.contains(result.getAdTypeId().longValue()))
                .sorted(Comparator.comparing(AdvertisingReportServiceImpl::scoreOf).reversed())
                .limit(8)
                .toList();

        String allowedChannelsSummary = safeAggregations(activeAdTypes).stream()
                .map(bucket -> bucket.getGroupValue() + " (" + bucket.getMatchingDocumentsCount() + ")")
                .collect(Collectors.joining(", "));

        return new ReportData(activeAdTypes, activeAds, recommendedAds, allowedChannelsSummary);
    }

    private List<VectorOglasResponse> fetchActiveAds(String category, Integer kampanjaId, String vectorTip) {
        String uri = UriComponentsBuilder.fromHttpUrl(vectorServiceBaseUrl + "/api/v1/oglasi")
                .queryParam("tip_oglasa", vectorTip)
                .queryParam("status", "active")
                .queryParam("kategorija", category)
                .queryParam("kampanja_id", kampanjaId)
                .queryParam("limit", 20)
                .queryParam("offset", 0)
                .toUriString();

        ResponseEntity<List<VectorOglasResponse>> response = restTemplate.exchange(
                uri,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );
        return response.getBody() == null ? List.of() : response.getBody();
    }

    private List<VectorOglasSearchResult> fetchSemanticResults(String category, String semanticQuery, Integer topK, String vectorTip) {
        VectorSemanticSearchRequest request = VectorSemanticSearchRequest.builder()
                .query(semanticQuery)
                .tipOglasa(vectorTip)
                .status("active")
                .kategorija(category)
                .topK(topK)
                .build();

        ResponseEntity<List<VectorOglasSearchResult>> response = restTemplate.exchange(
                vectorServiceBaseUrl + "/api/v1/oglasi/search/semantic-filtered",
                HttpMethod.POST,
                new HttpEntity<>(request),
                new ParameterizedTypeReference<>() {
                }
        );
        return response.getBody() == null ? List.of() : response.getBody();
    }

    private String mapContentTypeToVectorTip(String contentType) {
        return switch (contentType.toLowerCase(Locale.ROOT)) {
            case "image", "video" -> "vizuelni";
            default -> "tekstualni";
        };
    }

    private void addTitle(Document document) throws IOException {
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20, Font.BOLD);
        Paragraph title = new Paragraph("ADVERTISING REPORT", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);

        Font subtitleFont = FontFactory.getFont(FontFactory.HELVETICA, 10);
        Paragraph subtitle = new Paragraph(
                "Generated at " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                subtitleFont
        );
        subtitle.setAlignment(Element.ALIGN_CENTER);
        document.add(subtitle);
        document.add(new Paragraph(" "));
    }

    private void addFiltersSummary(Document document, String text, String category, String contentType, Integer kampanjaId, String semanticQuery, Integer topK)
            throws IOException {
        Font bodyFont = FontFactory.getFont(FontFactory.HELVETICA, 11);
        document.add(new Paragraph(
                String.format(Locale.ROOT,
                        "Ulazni parametri: text='%s', category='%s', contentType='%s', kampanjaId=%d, semanticQuery='%s', topK=%d.",
                        text, category, contentType, kampanjaId, semanticQuery, topK),
                bodyFont));
        document.add(new Paragraph(" "));
    }

    private void addSectionHeading(Document document, String title) throws IOException {
        Font sectionFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, Font.BOLD);
        Paragraph paragraph = new Paragraph(title, sectionFont);
        paragraph.setSpacingBefore(8f);
        paragraph.setSpacingAfter(6f);
        document.add(paragraph);
    }

    private void addParagraph(Document document, String text) throws IOException {
        Font bodyFont = FontFactory.getFont(FontFactory.HELVETICA, 10);
        Paragraph paragraph = new Paragraph(text, bodyFont);
        paragraph.setSpacingAfter(6f);
        document.add(paragraph);
    }

    private void addAdTypesTable(Document document, List<AdTypeResponse> adTypes) throws IOException {
        PdfPTable table = new PdfPTable(new float[]{1.0f, 2.4f, 1.2f, 1.5f, 1.2f, 1.0f});
        table.setWidthPercentage(100);
        addHeaderRow(table, "ID", "Naziv", "Kategorija", "Content type", "Kanal", "Trajanje");

        for (AdTypeResponse adType : adTypes) {
            table.addCell(cell(String.valueOf(adType.getId())));
            table.addCell(cell(adType.getName()));
            table.addCell(cell(adType.getCategory()));
            table.addCell(cell(adType.getContentType()));
            table.addCell(cell(adType.getTargetChannel()));
            table.addCell(cell(String.valueOf(adType.getAverageDurationDays())));
        }
        document.add(table);
    }

    private void addActiveAdsTable(Document document, List<VectorOglasResponse> ads) throws IOException {
        PdfPTable table = new PdfPTable(new float[]{1.0f, 2.2f, 1.2f, 1.2f, 1.2f, 1.0f});
        table.setWidthPercentage(100);
        addHeaderRow(table, "Oglas ID", "Naziv", "Tip", "Status", "Kategorija", "AdType ID");

        for (VectorOglasResponse ad : ads.stream().limit(12).toList()) {
            table.addCell(cell(String.valueOf(ad.getOglasId())));
            table.addCell(cell(ad.getNaziv()));
            table.addCell(cell(ad.getTipOglasa()));
            table.addCell(cell(ad.getStatus()));
            table.addCell(cell(ad.getKategorija()));
            table.addCell(cell(String.valueOf(ad.getAdTypeId())));
        }
        document.add(table);
    }

    private void addComplexResultsTable(Document document, List<VectorOglasSearchResult> ads, String allowedChannelsSummary) throws IOException {
        addParagraph(document, "Aktivni kanali dobijeni iz Elasticsearch agregacije: " + allowedChannelsSummary + ".");

        PdfPTable table = new PdfPTable(new float[]{1.0f, 2.2f, 1.2f, 1.2f, 1.0f, 1.0f});
        table.setWidthPercentage(100);
        addHeaderRow(table, "Oglas ID", "Naziv", "Tip", "Kategorija", "Score", "AdType ID");

        for (VectorOglasSearchResult ad : ads) {
            table.addCell(cell(String.valueOf(ad.getOglasId())));
            table.addCell(cell(ad.getNaziv()));
            table.addCell(cell(ad.getTipOglasa()));
            table.addCell(cell(ad.getKategorija()));
            table.addCell(cell(String.format(Locale.ROOT, "%.4f", scoreOf(ad))));
            table.addCell(cell(String.valueOf(ad.getAdTypeId())));
        }

        if (ads.isEmpty()) {
            table.addCell(cell("Nema preporucenih oglasa za zadate parametre.", 6));
        }

        document.add(table);
    }

    private void addDurationChart(Document document, List<AdTypeResponse> adTypes) throws IOException {
        if (adTypes == null || adTypes.isEmpty()) {
            addParagraph(document, "Grafikon nije moguce prikazati jer nema agregiranih rezultata.");
            return;
        }

        byte[] chartBytes = createDurationChart(adTypes.stream().limit(8).toList());
        Image image = Image.getInstance(chartBytes);
        image.scaleToFit(680, 260);
        image.setAlignment(Element.ALIGN_CENTER);
        document.add(image);
    }

    private void addSemanticScoreChart(Document document, List<VectorOglasSearchResult> ads) throws IOException {
        if (ads == null || ads.isEmpty()) {
            addParagraph(document, "Semanticki grafikon nije moguce prikazati jer nema preporucenih oglasa.");
            return;
        }

        byte[] chartBytes = createSemanticScoreChart(ads.stream().limit(8).toList());
        Image image = Image.getInstance(chartBytes);
        image.scaleToFit(680, 260);
        image.setAlignment(Element.ALIGN_CENTER);
        document.add(image);
    }

    private byte[] createDurationChart(List<AdTypeResponse> adTypes) throws IOException {
        int width = 900;
        int height = 320;
        int padding = 60;
        int chartHeight = height - 2 * padding;
        int chartWidth = width - 2 * padding;

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = image.createGraphics();
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, width, height);

        graphics.setColor(Color.DARK_GRAY);
        graphics.setStroke(new BasicStroke(2f));
        graphics.drawLine(padding, height - padding, width - padding, height - padding);
        graphics.drawLine(padding, padding, padding, height - padding);

        int maxValue = adTypes.stream()
                .map(AdTypeResponse::getAverageDurationDays)
                .filter(Objects::nonNull)
                .max(Integer::compareTo)
                .orElse(1);

        int barWidth = Math.max(40, chartWidth / adTypes.size() - 20);
        int gap = 20;
        int x = padding + 20;

        java.awt.Font labelFont = new java.awt.Font("SansSerif", java.awt.Font.PLAIN, 12);
        java.awt.Font titleFont = new java.awt.Font("SansSerif", java.awt.Font.BOLD, 16);
        graphics.setFont(titleFont);
        graphics.drawString("Displayed active ad types by average duration (days)", padding, 30);
        graphics.setFont(labelFont);
        FontMetrics metrics = graphics.getFontMetrics();

        for (AdTypeResponse adType : adTypes) {
            int duration = adType.getAverageDurationDays() == null ? 0 : adType.getAverageDurationDays();
            int barHeight = (int) ((duration * 1.0 / maxValue) * (chartHeight - 20));
            int y = height - padding - barHeight;
            graphics.setColor(BAR_COLOR);
            graphics.fillRect(x, y, barWidth, barHeight);

            graphics.setColor(Color.DARK_GRAY);
            String countLabel = String.valueOf(duration);
            graphics.drawString(countLabel, x + (barWidth - metrics.stringWidth(countLabel)) / 2, y - 6);

            String key = abbreviateLabel(adType.getName());
            graphics.drawString(key, x, height - padding + 18);
            x += barWidth + gap;
        }

        graphics.dispose();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(image, "png", outputStream);
        return outputStream.toByteArray();
    }

    private byte[] createSemanticScoreChart(List<VectorOglasSearchResult> ads) throws IOException {
        int width = 900;
        int height = 320;
        int padding = 60;
        int chartHeight = height - 2 * padding;
        int chartWidth = width - 2 * padding;

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = image.createGraphics();
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, width, height);

        graphics.setColor(Color.DARK_GRAY);
        graphics.setStroke(new BasicStroke(2f));
        graphics.drawLine(padding, height - padding, width - padding, height - padding);
        graphics.drawLine(padding, padding, padding, height - padding);

        double maxValue = ads.stream()
                .map(AdvertisingReportServiceImpl::scoreOf)
                .max(Double::compareTo)
                .orElse(1.0);

        int barWidth = Math.max(40, chartWidth / ads.size() - 20);
        int gap = 20;
        int x = padding + 20;

        java.awt.Font labelFont = new java.awt.Font("SansSerif", java.awt.Font.PLAIN, 12);
        java.awt.Font titleFont = new java.awt.Font("SansSerif", java.awt.Font.BOLD, 16);
        graphics.setFont(titleFont);
        graphics.drawString("Top recommended ads by semantic score", padding, 30);
        graphics.setFont(labelFont);
        FontMetrics metrics = graphics.getFontMetrics();

        for (VectorOglasSearchResult ad : ads) {
            double score = scoreOf(ad);
            int barHeight = maxValue == 0.0 ? 0 : (int) ((score / maxValue) * (chartHeight - 20));
            int y = height - padding - barHeight;
            graphics.setColor(new Color(57, 181, 74));
            graphics.fillRect(x, y, barWidth, barHeight);

            graphics.setColor(Color.DARK_GRAY);
            String scoreLabel = String.format(Locale.ROOT, "%.2f", score);
            graphics.drawString(scoreLabel, x + (barWidth - metrics.stringWidth(scoreLabel)) / 2, y - 6);

            String key = abbreviateLabel(ad.getNaziv());
            graphics.drawString(key, x, height - padding + 18);
            x += barWidth + gap;
        }

        graphics.dispose();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(image, "png", outputStream);
        return outputStream.toByteArray();
    }

    private String buildConclusion(ReportData reportData, String category, String contentType, Integer kampanjaId) {
        Map<String, Long> adsByType = reportData.activeAds().stream()
                .collect(Collectors.groupingBy(VectorOglasResponse::getTipOglasa, LinkedHashMap::new, Collectors.counting()));

        String dominantType = adsByType.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("nema");

        return String.format(Locale.ROOT,
                "Izvestaj pokazuje da za kategoriju '%s' i contentType '%s' kampanja %d ima %d aktivnih oglasa u vektorskoj bazi, " +
                        "dok Elasticsearch vraca %d aktivnih vrsta oglasa. Kao dominantan tip oglasa izdvojio se '%s', " +
                        "a slozeni upit je pronasao %d semanticki relevantnih preporuka.",
                category,
                contentType,
                kampanjaId,
                reportData.activeAds().size(),
                reportData.activeAdTypes().getTotalHits(),
                dominantType,
                reportData.recommendedAds().size());
    }

    private void addHeaderRow(PdfPTable table, String... headers) {
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10)));
            cell.setBackgroundColor(HEADER_BACKGROUND);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setPadding(6f);
            table.addCell(cell);
        }
    }

    private PdfPCell cell(String value) {
        return cell(value, 1);
    }

    private PdfPCell cell(String value, int colspan) {
        PdfPCell cell = new PdfPCell(new Phrase(value == null ? "" : value, FontFactory.getFont(FontFactory.HELVETICA, 9)));
        cell.setPadding(5f);
        cell.setColspan(colspan);
        return cell;
    }

    private static double scoreOf(VectorOglasSearchResult result) {
        if (result.getFusedScore() != null) {
            return result.getFusedScore();
        }
        return result.getScore() == null ? 0.0 : result.getScore();
    }

    private List<AdTypeResponse> safeAdTypeResults(AdTypeSearchQueryResponse response) {
        return response.getResults() == null ? List.of() : response.getResults();
    }

    private List<AggregationBucketResponse> safeAggregations(AdTypeSearchQueryResponse response) {
        return response.getGroupedByTargetChannel() == null ? List.of() : response.getGroupedByTargetChannel();
    }

    private String buildAdTypesSummary(long totalHits, int displayedCount, String text) {
        if (totalHits > displayedCount) {
            return String.format(
                    Locale.ROOT,
                    "Upit za tekst '%s' vratio je %d aktivnih vrsta oglasa u okviru servisnog limita od 50 rezultata. U tabeli je prikazano prvih %d rezultata, sortiranih po prosecnom trajanju.",
                    text,
                    totalHits,
                    displayedCount
            );
        }

        return String.format(
                Locale.ROOT,
                "Upit za tekst '%s' vratio je %d aktivnih vrsta oglasa i svi rezultati su prikazani u tabeli.",
                text,
                totalHits
        );
    }

    private String abbreviateLabel(String value) {
        if (value == null || value.isBlank()) {
            return "n/a";
        }

        String compact = value.length() <= 14 ? value : value.substring(0, 14) + "...";
        return compact.replace(' ', '_');
    }

    private record ReportData(
            AdTypeSearchQueryResponse activeAdTypes,
            List<VectorOglasResponse> activeAds,
            List<VectorOglasSearchResult> recommendedAds,
            String allowedChannelsSummary
    ) {
    }
}
