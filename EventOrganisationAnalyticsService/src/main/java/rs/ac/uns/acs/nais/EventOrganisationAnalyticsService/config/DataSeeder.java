package rs.ac.uns.acs.nais.EventOrganisationAnalyticsService.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import rs.ac.uns.acs.nais.EventOrganisationAnalyticsService.model.ReservationRequestDocument;
import rs.ac.uns.acs.nais.EventOrganisationAnalyticsService.model.ResourceUsageDocument;
import rs.ac.uns.acs.nais.EventOrganisationAnalyticsService.model.RequestedResourceItem;
import rs.ac.uns.acs.nais.EventOrganisationAnalyticsService.repository.ReservationRequestRepository;
import rs.ac.uns.acs.nais.EventOrganisationAnalyticsService.repository.ResourceUsageRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private final ReservationRequestRepository reservationRequestRepository;
    private final ResourceUsageRepository resourceUsageRepository;

    private final Random random = new Random(42);

    private static final String[][] STAGES = {
            {"stage-1", "Main Stage", "INDOOR"},
            {"stage-2", "Small Stage", "INDOOR"},
            {"stage-3", "Acoustic Stage", "INDOOR"},
            {"stage-4", "Open Air Stage", "OUTDOOR"},
            {"stage-5", "Jazz Stage", "INDOOR"}
    };

    private static final int[] STAGE_CAPACITIES = {1000, 300, 150, 2000, 200};

    private static final String[] FIRST_NAMES = {
            "Ana", "Mark", "Jane", "Stephen", "Mila",
            "Nick", "Tara", "Ian", "Bonnie", "Luke",
            "Helen", "Peter", "Sandra", "Alex", "Kate",
            "Diana", "Victor", "Nina", "Miles", "Tina"
    };

    private static final String[] LAST_NAMES = {
            "Johnson", "Peterson", "Nichols", "Marks", "George",
            "Stone", "Hill", "Pope", "Lane", "Stewart",
            "Vance", "Simon", "Paulson", "Reed", "Cooper",
            "Tate", "Duke", "Miller", "Swift", "Thomas"
    };

    private static final String[] GENRES = {
            "Rock", "Pop", "Jazz", "Folk", "Electronic",
            "Classical", "R&B", "Hip-hop", "Metal", "Blues"
    };

    private static final String[] REQUEST_STATUSES = {"PENDING", "APPROVED", "REJECTED"};

    private static final String[][] RESOURCES = {
            {"resource-1", "Sound System", "AUDIO", "false"},
            {"resource-2", "Wireless Microphone", "AUDIO", "true"},
            {"resource-3", "Mixer", "AUDIO", "true"},
            {"resource-4", "LED Screen", "VIDEO", "false"},
            {"resource-5", "Camera", "VIDEO", "true"},
            {"resource-6", "Projector", "VIDEO", "false"},
            {"resource-7", "Spotlight", "LIGHTING", "true"},
            {"resource-8", "Moving Head", "LIGHTING", "true"},
            {"resource-9", "Par Light", "LIGHTING", "true"},
            {"resource-10", "Smoke Machine", "EFFECTS", "true"},
            {"resource-11", "Confetti Cannon", "EFFECTS", "true"},
            {"resource-12", "CO2 Effect", "EFFECTS", "true"},
            {"resource-13", "Subwoofer", "AUDIO", "false"},
            {"resource-14", "Stage Monitor", "AUDIO", "true"},
            {"resource-15", "Hazer", "EFFECTS", "true"},
            {"resource-16", "LED Strip", "LIGHTING", "true"},
            {"resource-17", "Studio Light", "LIGHTING", "false"},
            {"resource-18", "Camera Tripod", "VIDEO", "true"},
            {"resource-19", "Wired Microphone", "AUDIO", "true"},
            {"resource-20", "DJ Controller", "AUDIO", "true"}
    };

    private static final String[] RESOURCE_STATUSES = {"AVAILABLE", "RESERVED"};

    private static final String[] REJECTION_REASONS = {
            "Resource is not available in the requested period",
            "Stage capacity does not support this resource",
            "Resource is reserved for another event"
    };

    private static final String[] NOTES = {
            "Please provide technical support during the performance",
            "Performer brings their own instruments",
            "Dressing room with mirror is required",
            "Special sound system requirements",
            "Performance includes special visual effects",
            "Band has five members",
            "International guest performance",
            "Festival performance with multiple performers",
            "Acoustic performance without amplification",
            null, null
    };

    private static final String[] PERFORMANCE_DETAILS = {
            "Electro-pop performance with light show",
            "Acoustic piano recital",
            "Rock band with full production",
            "Intimate jazz quartet performance",
            "Electronic DJ set",
            "Traditional folk group show",
            "Classical orchestra concert",
            "Heavy metal production",
            "Soulful R&B solo performance",
            "Live hip-hop collective performance",
            "Alternative indie band sound",
            "Retro blues trio atmosphere"
    };

    @Override
    public void run(String... args) {
        seedReservationRequests();
        seedResourceUsage();
    }

    private void seedReservationRequests() {
        if (reservationRequestRepository.count() > 0) {
            log.info("reservation-requests index already contains data, skipping seed.");
            return;
        }

        log.info("Seeding reservation-requests...");
        List<ReservationRequestDocument> docs = new ArrayList<>();
        LocalDate base = LocalDate.of(2024, 1, 1);

        for (int i = 0; i < 1100; i++) {
            int stageIdx = random.nextInt(5);
            String[] stage = STAGES[stageIdx];
            String status = REQUEST_STATUSES[random.nextInt(3)];
            boolean hasTasks = random.nextInt(3) > 0;
            int taskCount = hasTasks ? (1 + random.nextInt(7)) : 0;

            LocalDate sentDate = base.plusDays(random.nextInt(548));
            LocalDate performanceDate = sentDate.plusDays(5 + random.nextInt(60));
            int startHour = 17 + random.nextInt(5);
            int endHour = startHour + 1 + random.nextInt(3);

            docs.add(ReservationRequestDocument.builder()
                    .id(UUID.randomUUID().toString())
                    .requestStatus(status)
                    .sentDate(sentDate)
                    .updatedDate(sentDate.plusDays(random.nextInt(10)))
                    .note(pick(NOTES))
                    .stageId(stage[0])
                    .stageName(stage[1])
                    .stageType(stage[2])
                    .stageCapacity(STAGE_CAPACITIES[stageIdx])
                    .performerId("performer-" + (random.nextInt(50) + 1))
                    .performerFirstName(pick(FIRST_NAMES))
                    .performerLastName(pick(LAST_NAMES))
                    .genre(pick(GENRES))
                    .popularity(Math.round((1.0 + random.nextDouble() * 9.0) * 10.0) / 10.0)
                    .performanceDate(performanceDate)
                    .startTime(startHour)
                    .endTime(endHour)
                    .requestedResources(buildRequestedResources(hasTasks))
                    .hasTasks(hasTasks)
                    .taskCount(taskCount)
                    .performanceDetails(pick(PERFORMANCE_DETAILS))
                    .build());
        }

        reservationRequestRepository.saveAll(docs);
        log.info("Inserted {} reservation-requests documents.", docs.size());
    }

    private List<RequestedResourceItem> buildRequestedResources(boolean hasTasks) {
        int count = 1 + random.nextInt(4);
        List<RequestedResourceItem> list = new ArrayList<>();
        boolean forcedNonExistentAdded = false;

        for (int i = 0; i < count; i++) {
            String[] resource = RESOURCES[random.nextInt(RESOURCES.length)];

            boolean existsInSystem;
            if (hasTasks && !forcedNonExistentAdded && i == count - 1) {
                existsInSystem = false;
            } else {
                existsInSystem = !hasTasks || random.nextBoolean();
            }

            if (!existsInSystem) {
                forcedNonExistentAdded = true;
            }

            list.add(RequestedResourceItem.builder()
                    .resourceName(resource[1])
                    .resourceType(resource[2])
                    .requestedQuantity(1 + random.nextInt(5))
                    .existsInSystem(existsInSystem)
                    .resourceStatus(existsInSystem ? RESOURCE_STATUSES[random.nextInt(2)] : "UNAVAILABLE")
                    .rejectionReason(!existsInSystem ? pick(REJECTION_REASONS) : null)
                    .build());
        }

        return list;
    }

    private void seedResourceUsage() {
        if (resourceUsageRepository.count() > 0) {
            log.info("resource-usage index already contains data, skipping seed.");
            return;
        }

        log.info("Seeding resource-usage...");
        List<ResourceUsageDocument> docs = new ArrayList<>();
        LocalDate base = LocalDate.of(2024, 1, 1);

        for (int i = 0; i < 1200; i++) {
            int stageIdx = random.nextInt(5);
            String[] stage = STAGES[stageIdx];
            int resourceIdx = random.nextInt(RESOURCES.length);
            String[] resource = RESOURCES[resourceIdx];

            LocalDate date = base.plusDays(random.nextInt(548));
            int startHour = 17 + random.nextInt(5);
            int endHour = startHour + 1 + random.nextInt(3);

            boolean borrowed = random.nextInt(5) == 0;
            String borrowingStageName = null;
            if (borrowed) {
                int otherIdx = (stageIdx + 1 + random.nextInt(4)) % 5;
                borrowingStageName = STAGES[otherIdx][1];
            }

            docs.add(ResourceUsageDocument.builder()
                    .id(UUID.randomUUID().toString())
                    .resourceId(resource[0])
                    .resourceName(resource[1])
                    .resourceType(resource[2])
                    .portable(Boolean.parseBoolean(resource[3]))
                    .allocatedQuantity(1 + random.nextInt(15))
                    .stageId(stage[0])
                    .stageName(stage[1])
                    .stageType(stage[2])
                    .timeSlotId("time-slot-" + (random.nextInt(200) + 1))
                    .date(date)
                    .startTime(startHour)
                    .endTime(endHour)
                    .borrowedFromStage(borrowed)
                    .borrowingStageName(borrowingStageName)
                    .reservationId("reservation-" + (random.nextInt(500) + 1))
                    .build());
        }

        resourceUsageRepository.saveAll(docs);
        log.info("Inserted {} resource-usage documents.", docs.size());
    }

    private <T> T pick(T[] arr) {
        return arr[random.nextInt(arr.length)];
    }
}
