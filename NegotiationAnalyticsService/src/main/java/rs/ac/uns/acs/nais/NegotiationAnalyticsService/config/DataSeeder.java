package rs.ac.uns.acs.nais.NegotiationAnalyticsService.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import rs.ac.uns.acs.nais.NegotiationAnalyticsService.model.NegotiationStateHistory;
import rs.ac.uns.acs.nais.NegotiationAnalyticsService.model.OfferLifecycleEvent;
import rs.ac.uns.acs.nais.NegotiationAnalyticsService.service.NegotiationStateService;
import rs.ac.uns.acs.nais.NegotiationAnalyticsService.service.OfferEventService;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

// 160 pregovora koji prolaze kroz 7 stanja = 1120 slogova
// 160 ponuda koje prolaze kroz 7 stanja = 1120 slogova
@Component
public class DataSeeder implements CommandLineRunner {

    private final NegotiationStateService negotiationStateService;
    private final OfferEventService offerEventService;

    private static final String[] STATE_NAMES = {
            "INITIAL", "UNDER_REVIEW", "OFFER_SENT", "NEGOTIATING",
            "PENDING_APPROVAL", "CONTRACT_DRAFT", "FINALIZED"
    };

    private static final String[] TEMPLATES = {
            "STANDARD", "EXPRESS", "PREMIUM", "FESTIVAL"
    };

    private static final String[] MANAGERS = {
            "manager-001", "manager-002", "manager-003",
            "manager-004", "manager-005"
    };

    private static final String[] PERFORMERS = {
            "The Rolling Stones", "Coldplay", "Dua Lipa", "Ed Sheeran",
            "Billie Eilish", "Arctic Monkeys", "Kendrick Lamar", "Taylor Swift",
            "The Weeknd", "Harry Styles", "Adele", "Bruno Mars",
            "Post Malone", "Olivia Rodrigo", "Bad Bunny", "Drake"
    };

    private static final String[] STATUSES = {"CLOSED", "FAILED", "ACTIVE"};
    // da bi bili realisticniji podaci(vecina pregovora se uspesno zatvori)
    private static final String[] STATUS_WEIGHTS = {
            "CLOSED", "CLOSED", "CLOSED", "CLOSED",
            "FAILED", "FAILED",
            "ACTIVE"
    };

    private static final String[] EVENT_TYPES = {
            "CREATED", "PUBLISHED", "REVISED", "FROZEN",
            "COUNTER_OFFERED", "ACCEPTED", "CLOSED"
    };

    private static final String[] LOCATIONS = {
            "Novi Sad", "Beograd", "Nis", "Subotica",
            "Zagreb", "Ljubljana", "Sarajevo", "Skoplje"
    };

    public DataSeeder(NegotiationStateService negotiationStateService,
                      OfferEventService offerEventService) {
        this.negotiationStateService = negotiationStateService;
        this.offerEventService = offerEventService;
    }

    @Override
    public void run(String... args) {
        System.out.println("[DataSeeder] Starting InfluxDB seed...");

        Random rng = new Random(42);
        List<NegotiationStateHistory> stateRecords = new ArrayList<>();
        List<OfferLifecycleEvent> eventRecords = new ArrayList<>();

        Instant baseTime = Instant.now().minus(365, ChronoUnit.DAYS);

        int numNegotiations = 160;

        for (int i = 0; i < numNegotiations; i++) {
            String negotiationId = "neg-" + UUID.randomUUID().toString().substring(0, 8);
            String offerId = "offer-" + UUID.randomUUID().toString().substring(0, 8);
            String managerId = MANAGERS[rng.nextInt(MANAGERS.length)];
            String template = TEMPLATES[rng.nextInt(TEMPLATES.length)];
            String performer = PERFORMERS[rng.nextInt(PERFORMERS.length)];
            String status = STATUS_WEIGHTS[rng.nextInt(STATUS_WEIGHTS.length)];
            String location = LOCATIONS[rng.nextInt(LOCATIONS.length)];

            // svi zapisi su u rasponu od 12 meseci
            Instant negotiationStart = baseTime.plus(
                    rng.nextInt(360), ChronoUnit.DAYS
            ).plus(rng.nextInt(24 * 60), ChronoUnit.MINUTES);

            Instant cursor = negotiationStart;

            for (String stateName : STATE_NAMES) {
                // nasumicno dodeljujemo duzine trajanja po stanjima 1h - 7 dana
                long durationSeconds = 3600L + rng.nextInt(7 * 24 * 3600);

                NegotiationStateHistory record = new NegotiationStateHistory();
                record.setNegotiation_id(negotiationId);
                record.setState_name(stateName);
                record.setTemplate_name(template);
                record.setManager_id(managerId);
                record.setNegotiation_status(status);
                record.setDuration_seconds(durationSeconds);
                record.setPerformer_name(performer);
                record.setEntered_at(cursor);

                stateRecords.add(record);
                cursor = cursor.plus(durationSeconds, ChronoUnit.SECONDS);
            }
            
            // cene u rasponu od 5k - 50k
            double basePrice = 5000 + rng.nextInt(45000);
            Instant offerCursor = negotiationStart.plus(
                    rng.nextInt(48), ChronoUnit.HOURS
            );

            for (String eventType : EVENT_TYPES) {
                double price = basePrice + (rng.nextDouble() - 0.5) * 5000;

                OfferLifecycleEvent event = new OfferLifecycleEvent();
                event.setOffer_id(offerId);
                event.setEvent_type(eventType);
                event.setManager_id(managerId);
                event.setNegotiation_id(negotiationId);
                event.setPrice(Math.max(1000, price));
                event.setLocation(location);
                event.setOccurred_at(offerCursor);

                eventRecords.add(event);
                offerCursor = offerCursor.plus(
                        12 + rng.nextInt(72), ChronoUnit.HOURS
                );
            }
        }

        boolean stateOk = negotiationStateService.saveBatch(stateRecords);
        boolean eventOk = offerEventService.saveBatch(eventRecords);

        System.out.printf("[DataSeeder] negotiation_state_history: %d records — %s%n",
                stateRecords.size(), stateOk ? "OK" : "FAILED");
        System.out.printf("[DataSeeder] offer_lifecycle_events: %d records — %s%n",
                eventRecords.size(), eventOk ? "OK" : "FAILED");
    }
}
