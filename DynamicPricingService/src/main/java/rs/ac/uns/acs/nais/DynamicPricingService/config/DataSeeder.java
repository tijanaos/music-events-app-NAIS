package rs.ac.uns.acs.nais.DynamicPricingService.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import rs.ac.uns.acs.nais.DynamicPricingService.model.*;
import rs.ac.uns.acs.nais.DynamicPricingService.model.enums.CustomerTier;
import rs.ac.uns.acs.nais.DynamicPricingService.model.enums.PurchaseMethod;
import rs.ac.uns.acs.nais.DynamicPricingService.model.relationship.MadePurchase;
import rs.ac.uns.acs.nais.DynamicPricingService.model.relationship.ValidFor;
import rs.ac.uns.acs.nais.DynamicPricingService.repository.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private static final LocalDate REFERENCE_DATE = LocalDate.now();

    private final CustomerRepository customerRepository;
    private final PurchaseRepository purchaseRepository;
    private final TicketTypeRepository ticketTypeRepository;
    private final PromoCodeRepository promoCodeRepository;

    @Override
    @Transactional
    public void run(String... args) {
        if (ticketTypeRepository.count() > 0) return;

        TicketType[] tickets = seedTicketTypes();
        PromoCode[] promos = seedPromoCodes(tickets);
        Purchase[] purchases = seedPurchases(tickets, promos);
        seedCustomers(purchases);
    }

    private TicketType[] seedTicketTypes() {
        TicketType t1 = new TicketType(uuid(), "VIP All Access", 500, 450, List.of(
                new PriceSchedule(uuid(), "2026-01-01", "2026-02-28", 12000.0, 13500.0, 10000.0, 150, 185, 500.0),
                new PriceSchedule(uuid(), "2026-03-01", "2026-04-15", 13500.0, 15000.0, 10000.0, 200, 240, 500.0),
                new PriceSchedule(uuid(), "2026-04-16", "2026-05-31", 15000.0, 15000.0, 10000.0,   1,  12, 500.0)
        ));
        TicketType t2 = new TicketType(uuid(), "Regular", 10000, 2000, List.of(
                new PriceSchedule(uuid(), "2026-01-01", "2026-02-28",  4000.0,  4200.0, 3000.0, 500, 520, 200.0),
                new PriceSchedule(uuid(), "2026-03-01", "2026-04-15",  4500.0,  4500.0, 3000.0, 800, 750, 200.0),
                new PriceSchedule(uuid(), "2026-04-16", "2026-05-31",  5000.0,  5000.0, 3000.0,   1, 180, 200.0)
        ));
        TicketType t3 = new TicketType(uuid(), "Fan Pit Pass", 2000, 1800, List.of(
                new PriceSchedule(uuid(), "2026-01-01", "2026-02-28",  6000.0,  7200.0, 5500.0, 400, 520, 300.0),
                new PriceSchedule(uuid(), "2026-03-01", "2026-04-15",  7200.0,  8000.0, 5500.0, 600, 720, 300.0),
                new PriceSchedule(uuid(), "2026-04-16", "2026-05-31",  8000.0,  8000.0, 5500.0,   2,  80, 300.0)
        ));
        TicketType t4 = new TicketType(uuid(), "Backstage Experience", 50, 40, List.of(
                new PriceSchedule(uuid(), "2026-01-01", "2026-02-28", 20000.0, 22000.0, 18000.0, 12, 14, 1000.0),
                new PriceSchedule(uuid(), "2026-03-01", "2026-04-15", 22000.0, 24000.0, 18000.0, 10, 13, 1000.0),
                new PriceSchedule(uuid(), "2026-04-16", "2026-05-31", 25000.0, 25000.0, 18000.0,  1,  2, 1000.0)
        ));
        TicketType t5 = new TicketType(uuid(), "Day Pass", 5000, 500, List.of(
                new PriceSchedule(uuid(), "2026-01-01", "2026-02-28",  3000.0,  2800.0, 2000.0, 200, 170, 100.0),
                new PriceSchedule(uuid(), "2026-03-01", "2026-04-15",  3000.0,  2700.0, 2000.0, 200, 130, 100.0),
                new PriceSchedule(uuid(), "2026-04-16", "2026-05-31",  3000.0,  3000.0, 2000.0,   2,  25, 100.0)
        ));

        ticketTypeRepository.saveAll(List.of(t1, t2, t3, t4, t5));
        return new TicketType[]{t1, t2, t3, t4, t5};
    }

    private PromoCode[] seedPromoCodes(TicketType[] t) {
        PromoCode early2026 = new PromoCode(uuid(), "EARLY2026", 20, "2026-03-01", "2026-04-30", 500, 3, List.of(
                new ValidFor(null, 1, t[0]),
                new ValidFor(null, 1, t[3])
        ));
        PromoCode student10 = new PromoCode(uuid(), "STUDENT10", 10, "2026-01-01", "2026-07-15", 1000, 4, List.of(
                new ValidFor(null, 1, t[1]),
                new ValidFor(null, 1, t[4])
        ));
        PromoCode vip50off = new PromoCode(uuid(), "VIP50OFF", 15, "2026-05-01", "2026-06-01", 100, 1, List.of(
                new ValidFor(null, 1, t[0])
        ));
        PromoCode groupfun = new PromoCode(uuid(), "GROUPFUN", 25, "2026-04-01", "2026-06-15", null, 1, List.of(
                new ValidFor(null, 2, t[1]),
                new ValidFor(null, 1, t[2])
        ));

        promoCodeRepository.saveAll(List.of(early2026, student10, vip50off, groupfun));
        return new PromoCode[]{early2026, student10, vip50off, groupfun};
    }

    private Purchase[] seedPurchases(TicketType[] t, PromoCode[] pc) {
        // t[0]=VIP, t[1]=Regular, t[2]=FanPit, t[3]=Backstage, t[4]=DayPass
        // pc[0]=EARLY2026, pc[1]=STUDENT10, pc[2]=VIP50OFF, pc[3]=GROUPFUN

        Purchase[] p = new Purchase[]{
            new Purchase(uuid(), REFERENCE_DATE.minusDays(16).toString(), 2, 15000.0, 4500.0, 5100.0, 20400.0,  t[0], pc[0]),
            new Purchase(uuid(), REFERENCE_DATE.minusDays(14).toString(), 1,  8000.0, 1200.0,    0.0,  6800.0,  t[2], null),
            new Purchase(uuid(), REFERENCE_DATE.minusDays(18).toString(), 3,  5000.0, 1500.0, 1350.0, 12150.0,  t[1], pc[1]),
            new Purchase(uuid(), REFERENCE_DATE.minusDays(11).toString(), 1, 15000.0, 1500.0,    0.0, 13500.0,  t[0], null),
            new Purchase(uuid(), REFERENCE_DATE.minusDays(15).toString(), 2,  5000.0,  500.0,    0.0,  9500.0,  t[1], null),
            new Purchase(uuid(), REFERENCE_DATE.minusDays(12).toString(), 4,  3000.0,  600.0, 2850.0,  8550.0,  t[4], pc[3]),
            new Purchase(uuid(), REFERENCE_DATE.minusDays(21).toString(), 1, 25000.0, 3750.0,    0.0, 21250.0,  t[3], null),
            new Purchase(uuid(), REFERENCE_DATE.minusDays(20).toString(), 2, 15000.0, 4500.0,    0.0, 25500.0,  t[0], null),
            new Purchase(uuid(), REFERENCE_DATE.minusDays(13).toString(), 1,  3000.0,  150.0,    0.0,  2850.0,  t[4], null),
            new Purchase(uuid(), REFERENCE_DATE.minusDays(17).toString(), 2,  8000.0, 1600.0,    0.0, 14400.0,  t[2], null),
            new Purchase(uuid(), REFERENCE_DATE.minusDays(16).toString(), 1,  5000.0,  500.0,  450.0,  4050.0,  t[1], pc[1]),
            new Purchase(uuid(), REFERENCE_DATE.minusDays(10).toString(), 5,  5000.0, 1250.0,    0.0, 23750.0,  t[1], null),
            new Purchase(uuid(), REFERENCE_DATE.minusDays(19).toString(), 1, 15000.0, 2250.0, 2550.0, 10200.0,  t[0], pc[0]),
            new Purchase(uuid(), REFERENCE_DATE.minusDays(18).toString(), 1, 25000.0, 3750.0,    0.0, 21250.0,  t[3], null),
            new Purchase(uuid(), REFERENCE_DATE.minusDays(9).toString(),  1, 15000.0, 2250.0, 2550.0, 10200.0,  t[0], pc[0]),
            new Purchase(uuid(), REFERENCE_DATE.minusDays(8).toString(),  1,  8000.0, 1200.0,    0.0,  6800.0,  t[2], null),
            new Purchase(uuid(), REFERENCE_DATE.minusDays(3).toString(),  2,  5000.0, 1000.0,  900.0,  8100.0,  t[1], pc[1]),
            new Purchase(uuid(), REFERENCE_DATE.minusDays(2).toString(),  1, 25000.0, 2500.0,    0.0, 22500.0,  t[3], null),
            new Purchase(uuid(), REFERENCE_DATE.minusDays(1).toString(),  1,  3000.0,  150.0,  285.0,  2565.0,  t[4], pc[1]),
            new Purchase(uuid(), REFERENCE_DATE.minusDays(1).toString(),  3,  5000.0,  750.0,    0.0, 14250.0,  t[1], null),
            new Purchase(uuid(), REFERENCE_DATE.toString(),               1, 15000.0, 2250.0, 1912.5, 10837.5,  t[0], pc[2]),
            new Purchase(uuid(), REFERENCE_DATE.toString(),               1, 25000.0, 3750.0,    0.0, 21250.0,  t[3], null),
            new Purchase(uuid(), REFERENCE_DATE.toString(),               2,  5000.0,  250.0,    0.0,  9750.0,  t[1], null)
        };

        purchaseRepository.saveAll(List.of(p));
        return p;
    }

    private static String uuid() {
        return UUID.randomUUID().toString();
    }

    private void seedCustomers(Purchase[] p) {
        customerRepository.saveAll(List.of(
            new Customer(uuid(), "Marko Petrović",   "marko@email.com",   CustomerTier.GOLD,   List.of(new MadePurchase(null, PurchaseMethod.ONLINE,     p[0]),  new MadePurchase(null, PurchaseMethod.MOBILE_APP, p[1]))),
            new Customer(uuid(), "Ana Jovanović",    "ana@email.com",     CustomerTier.SILVER, List.of(new MadePurchase(null, PurchaseMethod.ONLINE,     p[2]),  new MadePurchase(null, PurchaseMethod.IN_PERSON,  p[3]))),
            new Customer(uuid(), "Nikola Đorđević",  "nikola@email.com",  CustomerTier.BRONZE, List.of(new MadePurchase(null, PurchaseMethod.MOBILE_APP, p[4]),  new MadePurchase(null, PurchaseMethod.ONLINE,     p[5]))),
            new Customer(uuid(), "Jelena Marković",  "jelena@email.com",  CustomerTier.GOLD,   List.of(new MadePurchase(null, PurchaseMethod.ONLINE,     p[6]),  new MadePurchase(null, PurchaseMethod.ONLINE,     p[7]))),
            new Customer(uuid(), "Stefan Ilić",      "stefan@email.com",  CustomerTier.BRONZE, List.of(new MadePurchase(null, PurchaseMethod.IN_PERSON,  p[8]))),
            new Customer(uuid(), "Milica Nikolić",   "milica@email.com",  CustomerTier.SILVER, List.of(new MadePurchase(null, PurchaseMethod.MOBILE_APP, p[9]),  new MadePurchase(null, PurchaseMethod.ONLINE,     p[10]))),
            new Customer(uuid(), "Đorđe Stanković",  "djordje@email.com", CustomerTier.BRONZE, List.of(new MadePurchase(null, PurchaseMethod.IN_PERSON,  p[11]), new MadePurchase(null, PurchaseMethod.MOBILE_APP, p[22]))),
            new Customer(uuid(), "Ivana Pavlović",   "ivana@email.com",   CustomerTier.GOLD,   List.of(new MadePurchase(null, PurchaseMethod.ONLINE,     p[12]), new MadePurchase(null, PurchaseMethod.ONLINE,     p[13]))),
            new Customer(uuid(), "Petar Lazović",    "petar@email.com",   CustomerTier.GOLD,   List.of(new MadePurchase(null, PurchaseMethod.ONLINE,     p[14]), new MadePurchase(null, PurchaseMethod.MOBILE_APP, p[15]))),
            new Customer(uuid(), "Maja Simić",       "maja@email.com",    CustomerTier.SILVER, List.of(new MadePurchase(null, PurchaseMethod.ONLINE,     p[16]), new MadePurchase(null, PurchaseMethod.MOBILE_APP, p[17]))),
            new Customer(uuid(), "Luka Vasiljević",  "luka@email.com",    CustomerTier.BRONZE, List.of(new MadePurchase(null, PurchaseMethod.IN_PERSON,  p[18]), new MadePurchase(null, PurchaseMethod.MOBILE_APP, p[19]))),
            new Customer(uuid(), "Sara Kovačević",   "sara@email.com",    CustomerTier.GOLD,   List.of(new MadePurchase(null, PurchaseMethod.ONLINE,     p[20]), new MadePurchase(null, PurchaseMethod.ONLINE,     p[21])))
        ));
    }
}