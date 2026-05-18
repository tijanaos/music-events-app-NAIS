package rs.ac.uns.acs.nais.AdvertisingSearchService.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import rs.ac.uns.acs.nais.AdvertisingSearchService.model.AdPhaseDocument;
import rs.ac.uns.acs.nais.AdvertisingSearchService.model.AdTypeDocument;
import rs.ac.uns.acs.nais.AdvertisingSearchService.repository.AdPhaseRepository;
import rs.ac.uns.acs.nais.AdvertisingSearchService.repository.AdTypeRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private static final int TARGET_SIZE = 1200;

    private final AdTypeRepository adTypeRepository;
    private final AdPhaseRepository adPhaseRepository;

    @Override
    public void run(String... args) {
        if (adTypeRepository.count() >= TARGET_SIZE && adPhaseRepository.count() >= TARGET_SIZE) {
            return;
        }

        adPhaseRepository.deleteAll();
        adTypeRepository.deleteAll();

        adTypeRepository.saveAll(buildAdTypes());
        adPhaseRepository.saveAll(buildAdPhases());
    }

    private List<AdTypeDocument> buildAdTypes() {
        String[] contentTypes = {"video", "image", "audio", "text"};
        String[] categories = {"festival", "performer", "sponsor", "venue", "ticketing"};
        String[] channels = {"social_media", "email", "billboard", "streaming_platform", "mobile_app"};
        String[] campaignDescriptors = {
                "video promocija", "brand awareness", "lead generation", "festival teaser", "community reach"
        };

        List<AdTypeDocument> documents = new ArrayList<>();
        for (long i = 1; i <= TARGET_SIZE; i++) {
            String contentType = contentTypes[(int) (i % contentTypes.length)];
            String category = categories[(int) (i % categories.length)];
            String descriptor = campaignDescriptors[(int) (i % campaignDescriptors.length)];
            String channel = channels[(int) (i % channels.length)];
            boolean active = i % 9 != 0;
            boolean requiresApproval = i % 3 != 0;

            if (i % 10 == 1) {
                contentType = "video";
                category = "festival";
                descriptor = "video promocija";
                channel = "social_media";
                active = true;
                requiresApproval = true;
            }

            documents.add(AdTypeDocument.builder()
                    .id(i)
                    .name(capitalize(contentType) + " oglas " + i)
                    .description("Oglas za " + descriptor + " kampanju u kategoriji " + category + " namenjen kanalu " + channel + ".")
                    .contentType(contentType)
                    .category(category)
                    .targetChannel(channel)
                    .isActive(active)
                    .requiresApproval(requiresApproval)
                    .averageDurationDays((int) (7 + (i % 24)))
                    .createdAt(LocalDate.of(2026, 5, 18).minusDays(i % 60))
                    .build());
        }
        return documents;
    }

    private List<AdPhaseDocument> buildAdPhases() {
        String[] phaseNames = {
                "Priprema briefa", "Početna provera", "Na čekanju za odobrenje", "Validacija budžeta",
                "Zakazivanje objave", "Finalna aktivacija"
        };
        String[] descriptions = {
                "Faza u kojoj tim priprema detaljan brief za oglas.",
                "Faza u kojoj se vrši provera sadržaja i tehničkih zahteva.",
                "Faza u kojoj oglas čeka odobrenje odgovorne osobe.",
                "Faza validacije budžeta i kanala distribucije.",
                "Faza zakazivanja objave po kanalima oglašavanja.",
                "Završna faza aktivacije i puštanja oglasa."
        };
        String[] roles = {
                "Marketing menadžer", "Content specijalista", "Brand menadžer", "Koordinator kampanje", "Pravni tim"
        };

        List<AdPhaseDocument> documents = new ArrayList<>();
        for (long i = 1; i <= TARGET_SIZE; i++) {
            int order = (int) ((i - 1) % 6) + 1;
            boolean finalPhase = order == 6;
            boolean active = i % 8 != 0;
            boolean requiresMail = order >= 2 && order <= 5;
            String phaseName = phaseNames[order - 1];
            String description = descriptions[order - 1];

            if (order == 2 || order == 3 || order == 4) {
                description += " Ova faza obuhvata proveru, odobrenje ili validaciju promotivnog materijala.";
            }

            documents.add(AdPhaseDocument.builder()
                    .id(i)
                    .adTypeId(((i - 1) % TARGET_SIZE) + 1)
                    .phaseName(phaseName)
                    .description(description)
                    .phaseOrder(order)
                    .responsibleRole(roles[(int) (i % roles.length)])
                    .requiresEmailNotification(requiresMail)
                    .isFinalPhase(finalPhase)
                    .isActive(active)
                    .expectedDurationHours(6 + (int) ((i * 3) % 48))
                    .createdAt(LocalDate.of(2026, 5, 18).minusDays(i % 45))
                    .build());
        }
        return documents;
    }

    private String capitalize(String value) {
        if (value == null || value.isBlank()) {
            return value;
        }
        return Character.toUpperCase(value.charAt(0)) + value.substring(1);
    }
}
