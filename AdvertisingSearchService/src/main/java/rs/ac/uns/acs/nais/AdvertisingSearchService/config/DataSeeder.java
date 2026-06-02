package rs.ac.uns.acs.nais.AdvertisingSearchService.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
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

    private static final int TARGET_SIZE = 1000;

    @Value("${advertising.seed.force-reload:true}")
    private boolean forceReload;

    private final AdTypeRepository adTypeRepository;
    private final AdPhaseRepository adPhaseRepository;

    @Override
    public void run(String... args) {
        if (!forceReload && adTypeRepository.count() >= TARGET_SIZE && adPhaseRepository.count() >= TARGET_SIZE) {
            return;
        }

        adPhaseRepository.deleteAll();
        adTypeRepository.deleteAll();

        adTypeRepository.saveAll(buildAdTypes());
        adPhaseRepository.saveAll(buildAdPhases());
    }

    private List<AdTypeDocument> buildAdTypes() {
        String[] contentTypes = {"video", "image", "audio", "text"};
        String[] categories = {"festival", "performer", "ticketing", "sponsor", "venue"};
        String[] channels = {"social_media", "streaming_platform", "email", "mobile_app", "billboard"};
        String[] descriptorPrefixes = {
                "sunset lineup reveal",
                "late-night stage teaser",
                "weekend pass push",
                "headline artist spotlight",
                "cashless wristband onboarding",
                "camping experience preview",
                "food village showcase",
                "sponsor activation highlight",
                "VIP terrace invitation",
                "opening day countdown"
        };
        String[] audienceAngles = {
                "first-time festival visitors",
                "electronic music fans",
                "indie and alternative listeners",
                "traveling friend groups",
                "students planning summer trips",
                "fans looking for premium access",
                "families visiting daytime sets",
                "brand partners and exhibitors",
                "regional live music communities",
                "mobile-first ticket buyers"
        };

        List<AdTypeDocument> documents = new ArrayList<>();
        for (long i = 1; i <= TARGET_SIZE; i++) {
            String contentType = contentTypes[(int) ((i - 1) % contentTypes.length)];
            String category = categories[(int) ((i - 1) % categories.length)];
            String channel = channels[(int) ((i - 1) % channels.length)];
            String descriptor = descriptorPrefixes[(int) ((i - 1) % descriptorPrefixes.length)];
            String audience = audienceAngles[(int) ((i * 3) % audienceAngles.length)];
            boolean active = i % 11 != 0;
            boolean requiresApproval = i % 4 != 0;

            documents.add(AdTypeDocument.builder()
                    .id(i)
                    .name(buildAdTypeName(contentType, descriptor, i))
                    .description(buildAdTypeDescription(contentType, category, channel, descriptor, audience))
                    .contentType(contentType)
                    .category(category)
                    .targetChannel(channel)
                    .isActive(active)
                    .requiresApproval(requiresApproval)
                    .averageDurationDays(5 + (int) (i % 18))
                    .createdAt(LocalDate.of(2026, 5, 18).minusDays(i % 90))
                    .build());
        }
        return documents;
    }

    private List<AdPhaseDocument> buildAdPhases() {
        String[] phaseNames = {
                "Creative Brief",
                "Asset Review",
                "Budget and Legal Check",
                "Audience Scheduling",
                "Channel Activation",
                "Post-Launch Monitoring"
        };
        String[] descriptions = {
                "The festival marketing team prepares the campaign goal, artist angle, and call to action.",
                "Visuals, copy, and media assets are reviewed to ensure they match the festival identity and lineup tone.",
                "Budget owners and legal stakeholders validate sponsor mentions, ticketing claims, and campaign constraints.",
                "The campaign is scheduled by audience cluster, city, and timing around lineup reveals or ticket drops.",
                "The ad is published across selected channels such as social media, streaming placements, and the festival app.",
                "Performance is monitored after launch to optimize click-through rate, conversion, and audience engagement."
        };
        String[] roles = {
                "Marketing Manager",
                "Content Producer",
                "Partnership Lead",
                "Ticketing Coordinator",
                "Venue Operations Lead",
                "Performance Analyst"
        };

        List<AdPhaseDocument> documents = new ArrayList<>();
        for (long i = 1; i <= TARGET_SIZE; i++) {
            int order = (int) ((i - 1) % phaseNames.length);
            boolean finalPhase = order == phaseNames.length - 1;
            boolean active = i % 9 != 0;
            boolean requiresMail = order >= 1 && order <= 4;

            documents.add(AdPhaseDocument.builder()
                    .id(i)
                    .adTypeId(i)
                    .phaseName(phaseNames[order])
                    .description(descriptions[order] + " This workflow step is tailored to a music festival promotion lifecycle.")
                    .phaseOrder(order + 1)
                    .responsibleRole(roles[(int) ((i - 1) % roles.length)])
                    .requiresEmailNotification(requiresMail)
                    .isFinalPhase(finalPhase)
                    .isActive(active)
                    .expectedDurationHours(8 + (int) ((i * 5) % 40))
                    .createdAt(LocalDate.of(2026, 5, 18).minusDays(i % 60))
                    .build());
        }
        return documents;
    }

    private String buildAdTypeName(String contentType, String descriptor, long id) {
        return capitalize(contentType) + " festival ad " + id + " - " + capitalizeWords(descriptor);
    }

    private String buildAdTypeDescription(String contentType, String category, String channel, String descriptor, String audience) {
        return "A " + contentType + " campaign template for music festivals focused on " + descriptor
                + ", built for the " + category + " segment and optimized for " + channel
                + ". The messaging is designed for " + audience + " while staying close enough to similar festival promotions for meaningful semantic search.";
    }

    private String capitalize(String value) {
        if (value == null || value.isBlank()) {
            return value;
        }
        return Character.toUpperCase(value.charAt(0)) + value.substring(1);
    }

    private String capitalizeWords(String value) {
        String[] parts = value.split(" ");
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < parts.length; i++) {
            if (i > 0) {
                builder.append(' ');
            }
            builder.append(capitalize(parts[i]));
        }
        return builder.toString();
    }
}
