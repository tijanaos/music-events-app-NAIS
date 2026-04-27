package rs.ac.uns.acs.nais.GraphDatabaseService.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import rs.ac.uns.acs.nais.GraphDatabaseService.model.*;
import rs.ac.uns.acs.nais.GraphDatabaseService.model.enums.*;
import rs.ac.uns.acs.nais.GraphDatabaseService.model.relationship.*;
import rs.ac.uns.acs.nais.GraphDatabaseService.repository.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private final StageRepository stageRepository;
    private final ResourceRepository resourceRepository;
    private final PerformerRepository performerRepository;
    private final TimeSlotRepository timeSlotRepository;
    private final ReservationRepository reservationRepository;

    @Override
    public void run(String... args) {
        if (stageRepository.count() > 0) {
            log.info("Database already contains data. Skipping seed.");
            return;
        }
        log.info("Starting database seed...");

        // ── Resources ────────────────────────────────────────────────────────
        Resource cameras = resourceRepository.save(Resource.builder()
                .name("Professional Camera Set").type(ResourceType.CAMERA)
                .quantity(10).portable(true)
                .description("4K broadcast cameras for live streaming").build());

        Resource soundSystem = resourceRepository.save(Resource.builder()
                .name("Main Sound System").type(ResourceType.SOUND_SYSTEM)
                .quantity(2).portable(false)
                .description("Premium PA system 50kW").build());

        Resource laserUnit = resourceRepository.save(Resource.builder()
                .name("Laser Effects Unit").type(ResourceType.SPECIAL_EFFECT)
                .quantity(5).portable(true)
                .description("Multi-beam laser effects system").build());

        Resource techCrew = resourceRepository.save(Resource.builder()
                .name("Technical Crew Alpha").type(ResourceType.TECHNICAL_STAFF)
                .quantity(8).portable(true)
                .description("Experienced sound and lighting technicians").build());

        Resource designTeam = resourceRepository.save(Resource.builder()
                .name("Stage Design Team").type(ResourceType.SCENOGRAPHY_STAFF)
                .quantity(6).portable(true)
                .description("Set and stage decoration experts").build());

        Resource ledScreens = resourceRepository.save(Resource.builder()
                .name("LED Screen Array").type(ResourceType.VISUALIZATION_STAFF)
                .quantity(3).portable(false)
                .description("Large format LED panels for visual effects").build());

        log.info("Saved 6 resources.");

        // ── Performers ────────────────────────────────────────────────────────
        Performer arcticMonkeys = performerRepository.save(Performer.builder()
                .name("Arctic Monkeys").genre("Rock").popularity(9.2)
                .averagePerformanceDuration(120).countryOfOrigin("United Kingdom").build());

        Performer theMidnight = performerRepository.save(Performer.builder()
                .name("The Midnight").genre("Electronic").popularity(8.5)
                .averagePerformanceDuration(90).countryOfOrigin("United States").build());

        Performer massiveAttack = performerRepository.save(Performer.builder()
                .name("Massive Attack").genre("Trip-Hop").popularity(8.9)
                .averagePerformanceDuration(100).countryOfOrigin("United Kingdom").build());

        Performer zedsDead = performerRepository.save(Performer.builder()
                .name("Zeds Dead").genre("Electronic").popularity(7.8)
                .averagePerformanceDuration(75).countryOfOrigin("Canada").build());

        Performer parcels = performerRepository.save(Performer.builder()
                .name("Parcels").genre("Indie Pop").popularity(7.2)
                .averagePerformanceDuration(60).countryOfOrigin("Australia").build());

        Performer foals = performerRepository.save(Performer.builder()
                .name("Foals").genre("Indie Rock").popularity(8.1)
                .averagePerformanceDuration(90).countryOfOrigin("United Kingdom").build());

        Performer bicepBand = performerRepository.save(Performer.builder()
                .name("Bicep").genre("Electronic").popularity(8.6)
                .averagePerformanceDuration(80).countryOfOrigin("United Kingdom").build());

        Performer portishead = performerRepository.save(Performer.builder()
                .name("Portishead").genre("Trip-Hop").popularity(9.0)
                .averagePerformanceDuration(110).countryOfOrigin("United Kingdom").build());

        log.info("Saved 8 performers.");

        // ── Time slots ────────────────────────────────────────────────────────
        LocalDate day1 = LocalDate.of(2026, 6, 20);
        LocalDate day2 = LocalDate.of(2026, 6, 21);
        LocalDate day3 = LocalDate.of(2026, 6, 22);

        TimeSlot slot1 = timeSlotRepository.save(TimeSlot.builder()
                .date(day1).startTime(day1.atTime(20, 0)).endTime(day1.atTime(22, 0))
                .durationMin(120).status(TimeSlotStatus.FREE).slotType(SlotType.EVENING).build());

        timeSlotRepository.save(TimeSlot.builder()
                .date(day1).startTime(day1.atTime(23, 0)).endTime(day2.atTime(1, 0))
                .durationMin(120).status(TimeSlotStatus.FREE).slotType(SlotType.NIGHT).build());

        timeSlotRepository.save(TimeSlot.builder()
                .date(day2).startTime(day2.atTime(10, 0)).endTime(day2.atTime(12, 0))
                .durationMin(120).status(TimeSlotStatus.BLOCKED).slotType(SlotType.MORNING).build());

        timeSlotRepository.save(TimeSlot.builder()
                .date(day2).startTime(day2.atTime(14, 0)).endTime(day2.atTime(16, 0))
                .durationMin(120).status(TimeSlotStatus.FREE).slotType(SlotType.AFTERNOON).build());

        TimeSlot slot5 = timeSlotRepository.save(TimeSlot.builder()
                .date(day2).startTime(day2.atTime(20, 0)).endTime(day2.atTime(22, 0))
                .durationMin(120).status(TimeSlotStatus.RESERVED).slotType(SlotType.EVENING).build());

        timeSlotRepository.save(TimeSlot.builder()
                .date(day2).startTime(day2.atTime(23, 0)).endTime(day3.atTime(1, 30))
                .durationMin(150).status(TimeSlotStatus.FREE).slotType(SlotType.NIGHT).build());

        timeSlotRepository.save(TimeSlot.builder()
                .date(day3).startTime(day3.atTime(15, 0)).endTime(day3.atTime(17, 0))
                .durationMin(120).status(TimeSlotStatus.FREE).slotType(SlotType.AFTERNOON).build());

        TimeSlot slot8 = timeSlotRepository.save(TimeSlot.builder()
                .date(day3).startTime(day3.atTime(21, 0)).endTime(day3.atTime(23, 30))
                .durationMin(150).status(TimeSlotStatus.RESERVED).slotType(SlotType.EVENING).build());

        TimeSlot slot9 = timeSlotRepository.save(TimeSlot.builder()
                .date(day1).startTime(day1.atTime(16, 0)).endTime(day1.atTime(18, 0))
                .durationMin(120).status(TimeSlotStatus.RESERVED).slotType(SlotType.AFTERNOON).build());

        TimeSlot slot10 = timeSlotRepository.save(TimeSlot.builder()
                .date(day2).startTime(day2.atTime(18, 0)).endTime(day2.atTime(20, 0))
                .durationMin(120).status(TimeSlotStatus.RESERVED).slotType(SlotType.EVENING).build());

        TimeSlot slot11 = timeSlotRepository.save(TimeSlot.builder()
                .date(day3).startTime(day3.atTime(18, 0)).endTime(day3.atTime(19, 30))
                .durationMin(90).status(TimeSlotStatus.RESERVED).slotType(SlotType.EVENING).build());

        TimeSlot slot12 = timeSlotRepository.save(TimeSlot.builder()
                .date(day1).startTime(day1.atTime(12, 0)).endTime(day1.atTime(14, 0))
                .durationMin(120).status(TimeSlotStatus.RESERVED).slotType(SlotType.AFTERNOON).build());

        log.info("Saved 12 time slots.");

        // ── Stages with HAS_RESOURCE relationships ────────────────────────────
        Stage mainStage = stageRepository.save(Stage.builder()
                .name("Main Stage").capacity(5000).type(StageType.MAIN)
                .location("Zone A - Central Field").active(true)
                .resources(List.of(
                        HasResource.builder().quantity(2).availableQuantity(2)
                                .addedDate(LocalDate.of(2026, 1, 15)).resource(soundSystem).build(),
                        HasResource.builder().quantity(3).availableQuantity(3)
                                .addedDate(LocalDate.of(2026, 1, 15)).resource(ledScreens).build(),
                        HasResource.builder().quantity(4).availableQuantity(3)
                                .addedDate(LocalDate.of(2026, 2, 1)).resource(techCrew).build()
                ))
                .build());

        Stage rockStage = stageRepository.save(Stage.builder()
                .name("Rock Stage").capacity(2000).type(StageType.SECONDARY)
                .location("Zone B - East Wing").active(true)
                .resources(List.of(
                        HasResource.builder().quantity(1).availableQuantity(1)
                                .addedDate(LocalDate.of(2026, 1, 20)).resource(soundSystem).build(),
                        HasResource.builder().quantity(5).availableQuantity(5)
                                .addedDate(LocalDate.of(2026, 2, 5)).resource(cameras).build(),
                        HasResource.builder().quantity(3).availableQuantity(2)
                                .addedDate(LocalDate.of(2026, 2, 5)).resource(laserUnit).build()
                ))
                .build());

        stageRepository.save(Stage.builder()
                .name("Acoustic Corner").capacity(500).type(StageType.SMALL)
                .location("Zone C - Garden Area").active(true)
                .resources(List.of(
                        HasResource.builder().quantity(3).availableQuantity(3)
                                .addedDate(LocalDate.of(2026, 3, 1)).resource(cameras).build(),
                        HasResource.builder().quantity(4).availableQuantity(4)
                                .addedDate(LocalDate.of(2026, 3, 1)).resource(designTeam).build()
                ))
                .build());

        // Main Stage shares laser units with Rock Stage (SHARES_RESOURCE)
        mainStage.setSharedResources(List.of(
                SharesResource.builder()
                        .resourceId(laserUnit.getId())
                        .sharingType(SharingType.TEMPORARY)
                        .dateFrom(LocalDate.of(2026, 6, 18))
                        .dateTo(LocalDate.of(2026, 6, 23))
                        .stage(rockStage)
                        .build()
        ));
        stageRepository.save(mainStage);

        log.info("Saved 3 stages.");

        // ── Reservations ──────────────────────────────────────────────────────
        reservationRepository.save(Reservation.builder()
                .status(ReservationStatus.APPROVED)
                .createdAt(LocalDateTime.of(2026, 4, 1, 10, 0))
                .updatedAt(LocalDateTime.of(2026, 4, 5, 14, 0))
                .note("Headline act - requires full production setup")
                .performanceDetails("{\"setLength\":120,\"bandMembers\":5,\"requiresSoundcheck\":true}")
                .createdBy("manager_john")
                .stage(OnStage.builder().confirmed(true).stage(mainStage).build())
                .timeSlot(OccupiesSlot.builder()
                        .reservationDate(LocalDate.of(2026, 4, 1)).systemSuggestion(false)
                        .timeSlot(slot5).build())
                .performer(ForPerformer.builder()
                        .managerUsername("manager_john").agreedFee(50000.0)
                        .performer(arcticMonkeys).build())
                .resources(List.of(
                        RequiresResource.builder().requestedQuantity(2)
                                .status(ResourceRequestStatus.APPROVED)
                                .existsInSystem(true).resource(soundSystem).build(),
                        RequiresResource.builder().requestedQuantity(3)
                                .status(ResourceRequestStatus.APPROVED)
                                .existsInSystem(true).resource(ledScreens).build()
                ))
                .build());

        reservationRepository.save(Reservation.builder()
                .status(ReservationStatus.CREATED)
                .createdAt(LocalDateTime.of(2026, 4, 10, 9, 30))
                .updatedAt(LocalDateTime.of(2026, 4, 10, 9, 30))
                .note("Electronic artist - needs special lighting")
                .performanceDetails("{\"setLength\":90,\"djSetup\":true,\"requiresLasers\":true}")
                .createdBy("manager_sara")
                .stage(OnStage.builder().confirmed(false).stage(rockStage).build())
                .timeSlot(OccupiesSlot.builder()
                        .reservationDate(LocalDate.of(2026, 4, 10)).systemSuggestion(true)
                        .timeSlot(slot8).build())
                .performer(ForPerformer.builder()
                        .managerUsername("manager_sara").agreedFee(18000.0)
                        .performer(theMidnight).build())
                .resources(List.of(
                        RequiresResource.builder().requestedQuantity(3)
                                .status(ResourceRequestStatus.PENDING)
                                .existsInSystem(true).resource(laserUnit).build(),
                        RequiresResource.builder().requestedQuantity(2)
                                .status(ResourceRequestStatus.PENDING)
                                .existsInSystem(false)
                                .managerNote("Need to rent externally")
                                .resource(techCrew).build()
                ))
                .build());

        reservationRepository.save(Reservation.builder()
                .status(ReservationStatus.PENDING)
                .createdAt(LocalDateTime.of(2026, 4, 12, 16, 0))
                .updatedAt(LocalDateTime.of(2026, 4, 13, 11, 0))
                .note("Trip-hop legends - atmospheric performance")
                .performanceDetails("{\"setLength\":100,\"requiresSmokeMachine\":true}")
                .createdBy("manager_john")
                .stage(OnStage.builder().confirmed(false).stage(mainStage).build())
                .timeSlot(OccupiesSlot.builder()
                        .reservationDate(LocalDate.of(2026, 4, 12)).systemSuggestion(false)
                        .timeSlot(slot1).build())
                .performer(ForPerformer.builder()
                        .managerUsername("manager_john").agreedFee(35000.0)
                        .performer(massiveAttack).build())
                .resources(List.of(
                        RequiresResource.builder().requestedQuantity(1)
                                .status(ResourceRequestStatus.REJECTED)
                                .existsInSystem(false)
                                .rejectionReason("Smoke machines not permitted in Zone A")
                                .resource(cameras).build()
                ))
                .build());

        reservationRepository.save(Reservation.builder()
                .status(ReservationStatus.APPROVED)
                .createdAt(LocalDateTime.of(2026, 3, 20, 9, 0))
                .updatedAt(LocalDateTime.of(2026, 3, 22, 11, 0))
                .note("Indie rock headliner - main stage opening act")
                .performanceDetails("{\"setLength\":90,\"bandMembers\":4,\"requiresSoundcheck\":true}")
                .createdBy("manager_sara")
                .stage(OnStage.builder().confirmed(false).stage(mainStage).build())
                .timeSlot(OccupiesSlot.builder()
                        .reservationDate(LocalDate.of(2026, 3, 20)).systemSuggestion(false)
                        .timeSlot(slot9).build())
                .performer(ForPerformer.builder()
                        .managerUsername("manager_sara").agreedFee(22000.0)
                        .performer(foals).build())
                .resources(List.of(
                        RequiresResource.builder().requestedQuantity(4)
                                .status(ResourceRequestStatus.APPROVED)
                                .existsInSystem(true).resource(techCrew).build()
                ))
                .build());

        reservationRepository.save(Reservation.builder()
                .status(ReservationStatus.APPROVED)
                .createdAt(LocalDateTime.of(2026, 3, 25, 14, 0))
                .updatedAt(LocalDateTime.of(2026, 3, 26, 10, 0))
                .note("Electronic headline DJ set")
                .performanceDetails("{\"setLength\":80,\"djSetup\":true,\"requiresLasers\":true}")
                .createdBy("manager_alex")
                .stage(OnStage.builder().confirmed(false).stage(rockStage).build())
                .timeSlot(OccupiesSlot.builder()
                        .reservationDate(LocalDate.of(2026, 3, 25)).systemSuggestion(true)
                        .timeSlot(slot10).build())
                .performer(ForPerformer.builder()
                        .managerUsername("manager_alex").agreedFee(30000.0)
                        .performer(bicepBand).build())
                .resources(List.of(
                        RequiresResource.builder().requestedQuantity(3)
                                .status(ResourceRequestStatus.APPROVED)
                                .existsInSystem(true).resource(laserUnit).build(),
                        RequiresResource.builder().requestedQuantity(3)
                                .status(ResourceRequestStatus.APPROVED)
                                .existsInSystem(true).resource(ledScreens).build()
                ))
                .build());

        reservationRepository.save(Reservation.builder()
                .status(ReservationStatus.APPROVED)
                .createdAt(LocalDateTime.of(2026, 4, 2, 10, 0))
                .updatedAt(LocalDateTime.of(2026, 4, 3, 16, 0))
                .note("Trip-hop legends closing the festival")
                .performanceDetails("{\"setLength\":110,\"requiresSmokeMachine\":true,\"requiresSoundcheck\":true}")
                .createdBy("manager_john")
                .stage(OnStage.builder().confirmed(true).stage(mainStage).build())
                .timeSlot(OccupiesSlot.builder()
                        .reservationDate(LocalDate.of(2026, 4, 2)).systemSuggestion(false)
                        .timeSlot(slot11).build())
                .performer(ForPerformer.builder()
                        .managerUsername("manager_john").agreedFee(60000.0)
                        .performer(portishead).build())
                .resources(List.of(
                        RequiresResource.builder().requestedQuantity(2)
                                .status(ResourceRequestStatus.APPROVED)
                                .existsInSystem(true).resource(soundSystem).build(),
                        RequiresResource.builder().requestedQuantity(5)
                                .status(ResourceRequestStatus.PENDING)
                                .existsInSystem(true)
                                .resource(cameras).build()
                ))
                .build());

        reservationRepository.save(Reservation.builder()
                .status(ReservationStatus.CANCELLED)
                .createdAt(LocalDateTime.of(2026, 3, 10, 8, 0))
                .updatedAt(LocalDateTime.of(2026, 3, 15, 12, 0))
                .note("Indie pop act - cancelled due to scheduling conflict")
                .performanceDetails("{\"setLength\":60,\"bandMembers\":5}")
                .createdBy("manager_sara")
                .stage(OnStage.builder().confirmed(false).stage(rockStage).build())
                .timeSlot(OccupiesSlot.builder()
                        .reservationDate(LocalDate.of(2026, 3, 10)).systemSuggestion(true)
                        .timeSlot(slot12).build())
                .performer(ForPerformer.builder()
                        .managerUsername("manager_sara").agreedFee(8000.0)
                        .performer(parcels).build())
                .resources(List.of(
                        RequiresResource.builder().requestedQuantity(2)
                                .status(ResourceRequestStatus.REJECTED)
                                .existsInSystem(false)
                                .rejectionReason("Budget constraints")
                                .resource(designTeam).build()
                ))
                .build());

        log.info("Saved 7 reservations.");
        log.info("Database seed completed: 3 stages, 6 resources, 8 performers, 12 time slots, 7 reservations.");
    }
}
