package rs.ac.uns.acs.nais.PerformerManagementService.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import rs.ac.uns.acs.nais.PerformerManagementService.model.*;
import rs.ac.uns.acs.nais.PerformerManagementService.model.enums.OfferStatus;
import rs.ac.uns.acs.nais.PerformerManagementService.model.relationship.*;
import rs.ac.uns.acs.nais.PerformerManagementService.repository.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private final PerformerRepository performerRepository;
    private final WorkflowTemplateRepository workflowTemplateRepository;
    private final StateRepository stateRepository;
    private final OfferRepository offerRepository;
    private final NegotiationRepository negotiationRepository;

    @Override
    public void run(String... args) {
        // Brisanje svega da bi osigurali čist start za demo
        negotiationRepository.deleteAll();
        offerRepository.deleteAll();
        performerRepository.deleteAll();
        workflowTemplateRepository.deleteAll();
        stateRepository.deleteAll();
        
        log.info("Starting database seed...");

        // ── States ─────────────────────────────────────────────────────────────
        State initial = stateRepository.save(State.builder()
                .name("Initial Contact").description("First contact made with performer or agent")
                .isInitial(true).isFinal(false).maxDurationDays(7).build());

        State offerSent = stateRepository.save(State.builder()
                .name("Offer Sent").description("Formal offer sent and awaiting response")
                .isInitial(false).isFinal(false).maxDurationDays(14).build());

        State inNegotiation = stateRepository.save(State.builder()
                .name("In Negotiation").description("Active negotiation of terms and conditions")
                .isInitial(false).isFinal(false).maxDurationDays(21).build());

        State contractReview = stateRepository.save(State.builder()
                .name("Contract Review").description("Contract drafted and under legal review")
                .isInitial(false).isFinal(false).maxDurationDays(10).build());

        State concluded = stateRepository.save(State.builder()
                .name("Concluded").description("Agreement reached and contract signed")
                .isInitial(false).isFinal(true).maxDurationDays(999).build());

        log.info("Saved 5 states.");

        // ── WorkflowTemplate ───────────────────────────────────────────────────
        WorkflowTemplate standardWorkflow = workflowTemplateRepository.save(
                WorkflowTemplate.builder()
                        .name("Standard Performer Workflow")
                        .description("Default workflow for negotiating with performers")
                        .archived(false)
                        .states(List.of(
                                ContainsState.builder().orderIndex(1).state(initial).build(),
                                ContainsState.builder().orderIndex(2).state(offerSent).build(),
                                ContainsState.builder().orderIndex(3).state(inNegotiation).build(),
                                ContainsState.builder().orderIndex(4).state(contractReview).build(),
                                ContainsState.builder().orderIndex(5).state(concluded).build()
                        ))
                        .build());

        log.info("Saved 1 workflow template.");

        // ── Performers ─────────────────────────────────────────────────────────
        Performer arcticMonkeys = performerRepository.save(Performer.builder()
                .name("Arctic Monkeys").genre("Rock").countryOfOrigin("United Kingdom")
                .memberCount(4).archived(false).build());

        Performer theMidnight = performerRepository.save(Performer.builder()
                .name("The Midnight").genre("Synthwave").countryOfOrigin("United States")
                .memberCount(2).archived(false).build());

        Performer massiveAttack = performerRepository.save(Performer.builder()
                .name("Massive Attack").genre("Trip-Hop").countryOfOrigin("United Kingdom")
                .memberCount(2).archived(false).build());

        Performer bicepDuo = performerRepository.save(Performer.builder()
                .name("Bicep").genre("Electronic").countryOfOrigin("United Kingdom")
                .memberCount(2).archived(false).build());

        Performer stromae = performerRepository.save(Performer.builder()
                .name("Stromae").genre("Electronic Pop").countryOfOrigin("Belgium")
                .memberCount(1).archived(false).build());

        Performer rammstein = performerRepository.save(Performer.builder()
                .name("Rammstein").genre("Industrial Metal").countryOfOrigin("Germany")
                .memberCount(6).archived(false).build());

        Performer radiohead = performerRepository.save(Performer.builder()
                .name("Radiohead").genre("Rock").countryOfOrigin("United Kingdom")
                .memberCount(5).archived(false).build());

        log.info("Saved 7 performers.");

        // ── Offers ─────────────────────────────────────────────────────────────
        Offer offer1 = offerRepository.save(Offer.builder()
                .price(85000.0).eventDate(LocalDate.of(2026, 7, 15))
                .location("Novi Sad, Serbia").duration(90)
                .additionalBenefits("Hotel accommodation for 4 nights, backstage catering")
                .status(OfferStatus.ACCEPTED).createdAt(LocalDateTime.now().minusDays(30))
                .publishedAt(LocalDateTime.now().minusDays(28))
                .workflowTemplate(BasedOn.builder().assignedAt(LocalDateTime.now().minusDays(30)).workflowTemplate(standardWorkflow).build())
                .build());

        Offer offer2 = offerRepository.save(Offer.builder()
                .price(55000.0).eventDate(LocalDate.of(2026, 7, 16))
                .location("Novi Sad, Serbia").duration(75)
                .additionalBenefits("Hotel accommodation for 2 nights")
                .status(OfferStatus.ACCEPTED).createdAt(LocalDateTime.now().minusDays(25))
                .publishedAt(LocalDateTime.now().minusDays(23))
                .workflowTemplate(BasedOn.builder().assignedAt(LocalDateTime.now().minusDays(25)).workflowTemplate(standardWorkflow).build())
                .build());

        Offer offer3 = offerRepository.save(Offer.builder()
                .price(70000.0).eventDate(LocalDate.of(2026, 7, 14))
                .location("Novi Sad, Serbia").duration(80)
                .additionalBenefits("Full technical rider covered")
                .status(OfferStatus.ACCEPTED).createdAt(LocalDateTime.now().minusDays(20))
                .publishedAt(LocalDateTime.now().minusDays(18))
                .workflowTemplate(BasedOn.builder().assignedAt(LocalDateTime.now().minusDays(20)).workflowTemplate(standardWorkflow).build())
                .build());

        Offer offer4 = offerRepository.save(Offer.builder()
                .price(120000.0).eventDate(LocalDate.of(2026, 7, 13))
                .location("Novi Sad, Serbia").duration(120)
                .additionalBenefits("Full production support, private transport")
                .status(OfferStatus.PUBLISHED).createdAt(LocalDateTime.now().minusDays(10))
                .publishedAt(LocalDateTime.now().minusDays(8))
                .workflowTemplate(BasedOn.builder().assignedAt(LocalDateTime.now().minusDays(10)).workflowTemplate(standardWorkflow).build())
                .build());

        Offer offer5 = offerRepository.save(Offer.builder()
                .price(45000.0).eventDate(LocalDate.of(2026, 7, 17))
                .location("Novi Sad, Serbia").duration(60)
                .status(OfferStatus.CREATED).createdAt(LocalDateTime.now().minusDays(3))
                .workflowTemplate(BasedOn.builder().assignedAt(LocalDateTime.now().minusDays(3)).workflowTemplate(standardWorkflow).build())
                .build());
        
        Offer offer6 = offerRepository.save(Offer.builder()
                .price(95000.0).eventDate(LocalDate.of(2026, 7, 18))
                .location("Novi Sad, Serbia").duration(100)
                .additionalBenefits("Full production support")
                .status(OfferStatus.ACCEPTED).createdAt(LocalDateTime.now().minusDays(45))
                .publishedAt(LocalDateTime.now().minusDays(43))
                .workflowTemplate(BasedOn.builder().assignedAt(LocalDateTime.now().minusDays(45)).workflowTemplate(standardWorkflow).build())
                .build());

        log.info("Saved 6 offers.");

        // ── Negotiations ───────────────────────────────────────────────────────
        // Negotiation 1: Arctic Monkeys — concluded
        negotiationRepository.save(Negotiation.builder()
                .createdBy("john.manager").createdAt(LocalDateTime.now().minusDays(28))
                .updatedAt(LocalDateTime.now().minusDays(2)).concludedAt(LocalDateTime.now().minusDays(2))
                .offer(CreatedFrom.builder().startedAt(LocalDateTime.now().minusDays(28)).offer(offer1).build())
                .currentState(InState.builder().enteredAt(LocalDateTime.now().minusDays(2)).state(concluded).build())
                .performers(List.of(PartOf.builder().agreedFee(82000.0).performer(arcticMonkeys).build()))
                .build());

        // Negotiation 2: The Midnight — in negotiation
        negotiationRepository.save(Negotiation.builder()
                .createdBy("john.manager").createdAt(LocalDateTime.now().minusDays(22))
                .updatedAt(LocalDateTime.now().minusDays(5))
                .offer(CreatedFrom.builder().startedAt(LocalDateTime.now().minusDays(22)).offer(offer2).build())
                .currentState(InState.builder().enteredAt(LocalDateTime.now().minusDays(5)).state(inNegotiation).build())
                .performers(List.of(PartOf.builder().agreedFee(53000.0).performer(theMidnight).build()))
                .build());

        // Negotiation 3: Massive Attack — failed
        negotiationRepository.save(Negotiation.builder()
                .createdBy("sara.manager").createdAt(LocalDateTime.now().minusDays(18))
                .updatedAt(LocalDateTime.now().minusDays(10))
                .failReason("Performer unavailable due to prior booking conflict")
                .failedAtStateName("In Negotiation")
                .offer(CreatedFrom.builder().startedAt(LocalDateTime.now().minusDays(18)).offer(offer3).build())
                .currentState(InState.builder().enteredAt(LocalDateTime.now().minusDays(10)).state(inNegotiation).build())
                .performers(List.of(PartOf.builder().agreedFee(0.0).performer(massiveAttack).build()))
                .build());

        // Negotiation 4: Bicep — contract review (stagnant — entered 25 days ago, max is 10)
        negotiationRepository.save(Negotiation.builder()
                .createdBy("sara.manager").createdAt(LocalDateTime.now().minusDays(40))
                .updatedAt(LocalDateTime.now().minusDays(25))
                .offer(CreatedFrom.builder().startedAt(LocalDateTime.now().minusDays(40)).offer(offer4).build())
                .currentState(InState.builder().enteredAt(LocalDateTime.now().minusDays(25)).state(contractReview).build())
                .performers(List.of(PartOf.builder().agreedFee(118000.0).performer(bicepDuo).build()))
                .build());

        // Negotiation 5: Stromae — concluded
        negotiationRepository.save(Negotiation.builder()
                .createdBy("sara.manager").createdAt(LocalDateTime.now().minusDays(60))
                .updatedAt(LocalDateTime.now().minusDays(15)).concludedAt(LocalDateTime.now().minusDays(15))
                .offer(CreatedFrom.builder().startedAt(LocalDateTime.now().minusDays(60)).offer(offer2).build())
                .currentState(InState.builder().enteredAt(LocalDateTime.now().minusDays(15)).state(concluded).build())
                .performers(List.of(PartOf.builder().agreedFee(44000.0).performer(stromae).build()))
                .build());

        // Negotiation 6: Rammstein — initial contact (stagnant — entered 12 days ago, max is 7)
        negotiationRepository.save(Negotiation.builder()
                .createdBy("john.manager").createdAt(LocalDateTime.now().minusDays(12))
                .updatedAt(LocalDateTime.now().minusDays(12))
                .offer(CreatedFrom.builder().startedAt(LocalDateTime.now().minusDays(12)).offer(offer5).build())
                .currentState(InState.builder().enteredAt(LocalDateTime.now().minusDays(12)).state(initial).build())
                .performers(List.of(PartOf.builder().agreedFee(0.0).performer(rammstein).build()))
                .build());

        // Negotiation 7: Radiohead — concluded (same genre as Arctic Monkeys → Q4 will show Rock with 2 negotiations)
        negotiationRepository.save(Negotiation.builder()
                .createdBy("john.manager").createdAt(LocalDateTime.now().minusDays(44))
                .updatedAt(LocalDateTime.now().minusDays(5)).concludedAt(LocalDateTime.now().minusDays(5))
                .offer(CreatedFrom.builder().startedAt(LocalDateTime.now().minusDays(44)).offer(offer6).build())
                .currentState(InState.builder().enteredAt(LocalDateTime.now().minusDays(5)).state(concluded).build())
                .performers(List.of(PartOf.builder().agreedFee(93000.0).performer(radiohead).build()))
                .build());

        log.info("Database seed completed successfully.");
    }
}
