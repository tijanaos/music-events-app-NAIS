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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    // Menjaj ovaj datum za testiranje na specifican dan.
    // Kada je postavljeno na LocalDate.now(), kupovine su uvek relativne prema danasnjjem datumu.
    private static final LocalDate REFERENCE_DATE = LocalDate.now();

    private final CustomerRepository customerRepository;
    private final PurchaseRepository purchaseRepository;
    private final TicketTypeRepository ticketTypeRepository;
    private final PromoCodeRepository promoCodeRepository;

    @Override
    @Transactional
    public void run(String... args) {
        if (ticketTypeRepository.count() > 0) return;

        Map<String, TicketType> ticketTypes = seedTicketTypes();
        Map<String, PromoCode> promoCodes = seedPromoCodes(ticketTypes);
        Map<String, Purchase> purchases = seedPurchases(ticketTypes, promoCodes);
        seedCustomers(purchases);
    }

    private Map<String, TicketType> seedTicketTypes() {
        // Svaki ticket tip ima 3 cenovnika:
        //   SCH-X01: 2026-01-01 → 2026-02-28 (proslo, rani popust)
        //   SCH-X02: 2026-03-01 → 2026-04-15 (proslo, redovna prodaja)
        //   SCH-X03: 2026-04-16 → 2026-05-31 (AKTIVAN danas 2026-04-26)
        // soldCount je ukupan broj prodatih karata ukljucujuci off-system prodaju.

        // TKT-001 VIP All Access — skoro rasprodato, cena raste
        TicketType t1 = new TicketType("TKT-001", "VIP All Access", 500, 450, List.of(
                new PriceSchedule("SCH-101", "2026-01-01", "2026-02-28", 12000.0, 13500.0, 10000.0, 150, 185, 500.0),
                new PriceSchedule("SCH-102", "2026-03-01", "2026-04-15", 13500.0, 15000.0, 10000.0, 200, 240, 500.0),
                new PriceSchedule("SCH-103", "2026-04-16", "2026-05-31", 15000.0, 15000.0, 10000.0,   1,  12, 500.0)
        ));

        // TKT-002 Regular — umerena prodaja
        TicketType t2 = new TicketType("TKT-002", "Regular", 10000, 2000, List.of(
                new PriceSchedule("SCH-201", "2026-01-01", "2026-02-28",  4000.0,  4200.0, 3000.0, 500, 520, 200.0),
                new PriceSchedule("SCH-202", "2026-03-01", "2026-04-15",  4500.0,  4500.0, 3000.0, 800, 750, 200.0),
                new PriceSchedule("SCH-203", "2026-04-16", "2026-05-31",  5000.0,  5000.0, 3000.0,   1, 180, 200.0)
        ));

        // TKT-003 Fan Pit Pass — visoka potraznja, cena kontinuirano rasla
        TicketType t3 = new TicketType("TKT-003", "Fan Pit Pass", 2000, 1800, List.of(
                new PriceSchedule("SCH-301", "2026-01-01", "2026-02-28",  6000.0,  7200.0, 5500.0, 400, 520, 300.0),
                new PriceSchedule("SCH-302", "2026-03-01", "2026-04-15",  7200.0,  8000.0, 5500.0, 600, 720, 300.0),
                new PriceSchedule("SCH-303", "2026-04-16", "2026-05-31",  8000.0,  8000.0, 5500.0,   2,  80, 300.0)
        ));

        // TKT-004 Backstage Experience — limitirano, luksuzno, cena agresivno rasla
        TicketType t4 = new TicketType("TKT-004", "Backstage Experience", 50, 40, List.of(
                new PriceSchedule("SCH-401", "2026-01-01", "2026-02-28", 20000.0, 22000.0, 18000.0, 12, 14, 1000.0),
                new PriceSchedule("SCH-402", "2026-03-01", "2026-04-15", 22000.0, 24000.0, 18000.0, 10, 13, 1000.0),
                new PriceSchedule("SCH-403", "2026-04-16", "2026-05-31", 25000.0, 25000.0, 18000.0,  1,  2, 1000.0)
        ));

        // TKT-005 Day Pass — slaba prodaja, cena pada
        TicketType t5 = new TicketType("TKT-005", "Day Pass", 5000, 500, List.of(
                new PriceSchedule("SCH-501", "2026-01-01", "2026-02-28",  3000.0,  2800.0, 2000.0, 200, 170, 100.0),
                new PriceSchedule("SCH-502", "2026-03-01", "2026-04-15",  3000.0,  2700.0, 2000.0, 200, 130, 100.0),
                new PriceSchedule("SCH-503", "2026-04-16", "2026-05-31",  3000.0,  3000.0, 2000.0,   2,  25, 100.0)
        ));

        ticketTypeRepository.saveAll(List.of(t1, t2, t3, t4, t5));

        Map<String, TicketType> map = new HashMap<>();
        map.put("TKT-001", t1); map.put("TKT-002", t2); map.put("TKT-003", t3);
        map.put("TKT-004", t4); map.put("TKT-005", t5);
        return map;
    }

    private Map<String, PromoCode> seedPromoCodes(Map<String, TicketType> t) {
        PromoCode early2026 = new PromoCode("EARLY2026", 20, "2026-03-01", "2026-04-30", 500, 3, List.of(
                new ValidFor(null, 1, t.get("TKT-001")),
                new ValidFor(null, 1, t.get("TKT-004"))
        ));

        PromoCode student10 = new PromoCode("STUDENT10", 10, "2026-01-01", "2026-07-15", 1000, 4, List.of(
                new ValidFor(null, 1, t.get("TKT-002")),
                new ValidFor(null, 1, t.get("TKT-005"))
        ));

        PromoCode vip50off = new PromoCode("VIP50OFF", 15, "2026-05-01", "2026-06-01", 100, 1, List.of(
                new ValidFor(null, 1, t.get("TKT-001"))
        ));

        PromoCode groupfun = new PromoCode("GROUPFUN", 25, "2026-04-01", "2026-06-15", null, 1, List.of(
                new ValidFor(null, 2, t.get("TKT-002")),
                new ValidFor(null, 1, t.get("TKT-003"))
        ));

        promoCodeRepository.saveAll(List.of(early2026, student10, vip50off, groupfun));

        Map<String, PromoCode> map = new HashMap<>();
        map.put("EARLY2026", early2026); map.put("STUDENT10", student10);
        map.put("VIP50OFF", vip50off);   map.put("GROUPFUN", groupfun);
        return map;
    }

    private Map<String, Purchase> seedPurchases(Map<String, TicketType> t, Map<String, PromoCode> pc) {
        // Datumi su relativni prema REFERENCE_DATE.
        // Kupovine unutar 48h prozora (minusDays 0, 1, 2): PUR-018, PUR-019, PUR-020, PUR-021, PUR-022.

        Purchase pur1  = new Purchase("PUR-001", REFERENCE_DATE.minusDays(16).toString(), 2, 15000.0, 4500.0, 5100.0, 20400.0, t.get("TKT-001"), pc.get("EARLY2026"));
        Purchase pur2  = new Purchase("PUR-002", REFERENCE_DATE.minusDays(14).toString(), 1,  8000.0, 1200.0,    0.0,  6800.0, t.get("TKT-003"), null);
        Purchase pur3  = new Purchase("PUR-003", REFERENCE_DATE.minusDays(18).toString(), 3,  5000.0, 1500.0, 1350.0, 12150.0, t.get("TKT-002"), pc.get("STUDENT10"));
        Purchase pur4  = new Purchase("PUR-004", REFERENCE_DATE.minusDays(11).toString(), 1, 15000.0, 1500.0,    0.0, 13500.0, t.get("TKT-001"), null);
        Purchase pur5  = new Purchase("PUR-005", REFERENCE_DATE.minusDays(15).toString(), 2,  5000.0,  500.0,    0.0,  9500.0, t.get("TKT-002"), null);
        Purchase pur6  = new Purchase("PUR-006", REFERENCE_DATE.minusDays(12).toString(), 4,  3000.0,  600.0, 2850.0,  8550.0, t.get("TKT-005"), pc.get("GROUPFUN"));
        Purchase pur7  = new Purchase("PUR-007", REFERENCE_DATE.minusDays(21).toString(), 1, 25000.0, 3750.0,    0.0, 21250.0, t.get("TKT-004"), null);
        Purchase pur8  = new Purchase("PUR-008", REFERENCE_DATE.minusDays(20).toString(), 2, 15000.0, 4500.0,    0.0, 25500.0, t.get("TKT-001"), null);
        Purchase pur9  = new Purchase("PUR-009", REFERENCE_DATE.minusDays(13).toString(), 1,  3000.0,  150.0,    0.0,  2850.0, t.get("TKT-005"), null);
        Purchase pur10 = new Purchase("PUR-010", REFERENCE_DATE.minusDays(17).toString(), 2,  8000.0, 1600.0,    0.0, 14400.0, t.get("TKT-003"), null);
        Purchase pur11 = new Purchase("PUR-011", REFERENCE_DATE.minusDays(16).toString(), 1,  5000.0,  500.0,  450.0,  4050.0, t.get("TKT-002"), pc.get("STUDENT10"));
        Purchase pur12 = new Purchase("PUR-012", REFERENCE_DATE.minusDays(10).toString(), 5,  5000.0, 1250.0,    0.0, 23750.0, t.get("TKT-002"), null);
        Purchase pur13 = new Purchase("PUR-013", REFERENCE_DATE.minusDays(19).toString(), 1, 15000.0, 2250.0, 2550.0, 10200.0, t.get("TKT-001"), pc.get("EARLY2026"));
        Purchase pur14 = new Purchase("PUR-014", REFERENCE_DATE.minusDays(18).toString(), 1, 25000.0, 3750.0,    0.0, 21250.0, t.get("TKT-004"), null);
        Purchase pur15 = new Purchase("PUR-015", REFERENCE_DATE.minusDays(9).toString(),  1, 15000.0, 2250.0, 2550.0, 10200.0, t.get("TKT-001"), pc.get("EARLY2026"));
        Purchase pur16 = new Purchase("PUR-016", REFERENCE_DATE.minusDays(8).toString(),  1,  8000.0, 1200.0,    0.0,  6800.0, t.get("TKT-003"), null);
        Purchase pur17 = new Purchase("PUR-017", REFERENCE_DATE.minusDays(3).toString(),  2,  5000.0, 1000.0,  900.0,  8100.0, t.get("TKT-002"), pc.get("STUDENT10"));
        // Kupovine unutar poslednjih 48h
        Purchase pur18 = new Purchase("PUR-018", REFERENCE_DATE.minusDays(2).toString(),  1, 25000.0, 2500.0,    0.0, 22500.0, t.get("TKT-004"), null);
        Purchase pur19 = new Purchase("PUR-019", REFERENCE_DATE.minusDays(1).toString(),  1,  3000.0,  150.0,  285.0,  2565.0, t.get("TKT-005"), pc.get("STUDENT10"));
        Purchase pur20 = new Purchase("PUR-020", REFERENCE_DATE.minusDays(1).toString(),  3,  5000.0,  750.0,    0.0, 14250.0, t.get("TKT-002"), null);
        Purchase pur21 = new Purchase("PUR-021", REFERENCE_DATE.toString(),               1, 15000.0, 2250.0, 1912.5, 10837.5, t.get("TKT-001"), pc.get("VIP50OFF"));
        Purchase pur22 = new Purchase("PUR-022", REFERENCE_DATE.toString(),               1, 25000.0, 3750.0,    0.0, 21250.0, t.get("TKT-004"), null);
        Purchase pur23 = new Purchase("PUR-023", REFERENCE_DATE.toString(),               2,  5000.0,  250.0,    0.0,  9750.0, t.get("TKT-002"), null);

        purchaseRepository.saveAll(List.of(
                pur1,  pur2,  pur3,  pur4,  pur5,  pur6,  pur7,  pur8,  pur9,  pur10,
                pur11, pur12, pur13, pur14, pur15, pur16, pur17, pur18, pur19, pur20,
                pur21, pur22, pur23
        ));

        Map<String, Purchase> map = new HashMap<>();
        map.put("PUR-001", pur1);  map.put("PUR-002", pur2);  map.put("PUR-003", pur3);
        map.put("PUR-004", pur4);  map.put("PUR-005", pur5);  map.put("PUR-006", pur6);
        map.put("PUR-007", pur7);  map.put("PUR-008", pur8);  map.put("PUR-009", pur9);
        map.put("PUR-010", pur10); map.put("PUR-011", pur11); map.put("PUR-012", pur12);
        map.put("PUR-013", pur13); map.put("PUR-014", pur14); map.put("PUR-015", pur15);
        map.put("PUR-016", pur16); map.put("PUR-017", pur17); map.put("PUR-018", pur18);
        map.put("PUR-019", pur19); map.put("PUR-020", pur20); map.put("PUR-021", pur21);
        map.put("PUR-022", pur22); map.put("PUR-023", pur23);
        return map;
    }

    private void seedCustomers(Map<String, Purchase> p) {
        Customer marko  = new Customer("CUS-001", "Marko Petrović",   "marko@email.com",   CustomerTier.GOLD,   List.of(
                new MadePurchase(null, PurchaseMethod.ONLINE,     p.get("PUR-001")),
                new MadePurchase(null, PurchaseMethod.MOBILE_APP, p.get("PUR-002"))
        ));
        Customer ana    = new Customer("CUS-002", "Ana Jovanović",    "ana@email.com",      CustomerTier.SILVER, List.of(
                new MadePurchase(null, PurchaseMethod.ONLINE,     p.get("PUR-003")),
                new MadePurchase(null, PurchaseMethod.IN_PERSON,  p.get("PUR-004"))
        ));
        Customer nikola = new Customer("CUS-003", "Nikola Đorđević",  "nikola@email.com",   CustomerTier.BRONZE, List.of(
                new MadePurchase(null, PurchaseMethod.MOBILE_APP, p.get("PUR-005")),
                new MadePurchase(null, PurchaseMethod.ONLINE,     p.get("PUR-006"))
        ));
        Customer jelena = new Customer("CUS-004", "Jelena Marković",  "jelena@email.com",   CustomerTier.GOLD,   List.of(
                new MadePurchase(null, PurchaseMethod.ONLINE,     p.get("PUR-007")),
                new MadePurchase(null, PurchaseMethod.ONLINE,     p.get("PUR-008"))
        ));
        Customer stefan = new Customer("CUS-005", "Stefan Ilić",      "stefan@email.com",   CustomerTier.BRONZE, List.of(
                new MadePurchase(null, PurchaseMethod.IN_PERSON,  p.get("PUR-009"))
        ));
        Customer milica = new Customer("CUS-006", "Milica Nikolić",   "milica@email.com",   CustomerTier.SILVER, List.of(
                new MadePurchase(null, PurchaseMethod.MOBILE_APP, p.get("PUR-010")),
                new MadePurchase(null, PurchaseMethod.ONLINE,     p.get("PUR-011"))
        ));
        Customer djordje = new Customer("CUS-007", "Đorđe Stanković", "djordje@email.com",  CustomerTier.BRONZE, List.of(
                new MadePurchase(null, PurchaseMethod.IN_PERSON,  p.get("PUR-012")),
                new MadePurchase(null, PurchaseMethod.MOBILE_APP, p.get("PUR-023"))
        ));
        Customer ivana  = new Customer("CUS-008", "Ivana Pavlović",   "ivana@email.com",    CustomerTier.GOLD,   List.of(
                new MadePurchase(null, PurchaseMethod.ONLINE,     p.get("PUR-013")),
                new MadePurchase(null, PurchaseMethod.ONLINE,     p.get("PUR-014"))
        ));
        Customer petar  = new Customer("CUS-009", "Petar Lazović",    "petar@email.com",    CustomerTier.GOLD,   List.of(
                new MadePurchase(null, PurchaseMethod.ONLINE,     p.get("PUR-015")),
                new MadePurchase(null, PurchaseMethod.MOBILE_APP, p.get("PUR-016"))
        ));
        Customer maja   = new Customer("CUS-010", "Maja Simić",       "maja@email.com",     CustomerTier.SILVER, List.of(
                new MadePurchase(null, PurchaseMethod.ONLINE,     p.get("PUR-017")),
                new MadePurchase(null, PurchaseMethod.MOBILE_APP, p.get("PUR-018"))
        ));
        Customer luka   = new Customer("CUS-011", "Luka Vasiljević",  "luka@email.com",     CustomerTier.BRONZE, List.of(
                new MadePurchase(null, PurchaseMethod.IN_PERSON,  p.get("PUR-019")),
                new MadePurchase(null, PurchaseMethod.MOBILE_APP, p.get("PUR-020"))
        ));
        Customer sara   = new Customer("CUS-012", "Sara Kovačević",   "sara@email.com",     CustomerTier.GOLD,   List.of(
                new MadePurchase(null, PurchaseMethod.ONLINE,     p.get("PUR-021")),
                new MadePurchase(null, PurchaseMethod.ONLINE,     p.get("PUR-022"))
        ));

        customerRepository.saveAll(List.of(marko, ana, nikola, jelena, stefan, milica, djordje, ivana, petar, maja, luka, sara));
    }
}
