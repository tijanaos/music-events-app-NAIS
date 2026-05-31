package rs.ac.uns.acs.nais.AdvertisingSearchService.service;

import java.io.IOException;

public interface AdvertisingReportService {
    byte[] export(String text, String category, String contentType, Integer kampanjaId, String semanticQuery, Integer topK) throws IOException;
}
