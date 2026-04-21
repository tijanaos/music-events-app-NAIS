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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

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
        TicketType t1 = new TicketType("TKT-001", "VIP All Access", 15000.0, 10000.0, 15000.0, 500, 6, 50, "2026-06-01", "2026-06-07");
        TicketType t2 = new TicketType("TKT-002", "Regular", 5000.0, 3000.0, 5000.0, 10000, 11, 200, "2026-06-01", "2026-06-07");
        TicketType t3 = new TicketType("TKT-003", "Fan Pit Pass", 8000.0, 5500.0, 8000.0, 2000, 3, 100, "2026-06-01", "2026-06-07");
        TicketType t4 = new TicketType("TKT-004", "Backstage Experience", 25000.0, 18000.0, 25000.0, 50, 2, 10, "2026-06-01", "2026-06-07");
        TicketType t5 = new TicketType("TKT-005", "Day Pass", 3000.0, 2000.0, 3000.0, 5000, 5, 300, "2026-06-01", "2026-06-07");

        ticketTypeRepository.saveAll(List.of(t1, t2, t3, t4, t5));

        Map<String, TicketType> map = new HashMap<>();
        map.put("TKT-001", t1);
        map.put("TKT-002", t2);
        map.put("TKT-003", t3);
        map.put("TKT-004", t4);
        map.put("TKT-005", t5);
        return map;
    }

    private Map<String, PromoCode> seedPromoCodes(Map<String, TicketType> t) {
        PromoCode early2026 = new PromoCode("EARLY2026", 20, "2026-03-01", "2026-04-30", 500, 2, List.of(
                new ValidFor(null, 1, t.get("TKT-001")),
                new ValidFor(null, 1, t.get("TKT-004"))
        ));

        PromoCode student10 = new PromoCode("STUDENT10", 10, "2026-01-01", "2026-07-15", 1000, 2, List.of(
                new ValidFor(null, 1, t.get("TKT-002")),
                new ValidFor(null, 1, t.get("TKT-005"))
        ));

        PromoCode vip50off = new PromoCode("VIP50OFF", 15, "2026-05-01", "2026-06-01", 100, 0, List.of(
                new ValidFor(null, 1, t.get("TKT-001"))
        ));

        PromoCode groupfun = new PromoCode("GROUPFUN", 25, "2026-04-01", "2026-06-15", null, 1, List.of(
                new ValidFor(null, 2, t.get("TKT-002")),
                new ValidFor(null, 1, t.get("TKT-003"))
        ));

        promoCodeRepository.saveAll(List.of(early2026, student10, vip50off, groupfun));

        Map<String, PromoCode> map = new HashMap<>();
        map.put("EARLY2026", early2026);
        map.put("STUDENT10", student10);
        map.put("VIP50OFF", vip50off);
        map.put("GROUPFUN", groupfun);
        return map;
    }

    private Map<String, Purchase> seedPurchases(Map<String, TicketType> t, Map<String, PromoCode> pc) {
        Purchase pur1  = new Purchase("PUR-001", "2026-04-10", 2, 15000.0, 4500.0, 5100.0, 20400.0,  t.get("TKT-001"), pc.get("EARLY2026"));
        Purchase pur2  = new Purchase("PUR-002", "2026-04-12", 1, 8000.0,  1200.0, 0.0,    6800.0,   t.get("TKT-003"), null);
        Purchase pur3  = new Purchase("PUR-003", "2026-04-08", 3, 5000.0,  1500.0, 1350.0, 12150.0,  t.get("TKT-002"), pc.get("STUDENT10"));
        Purchase pur4  = new Purchase("PUR-004", "2026-04-15", 1, 15000.0, 1500.0, 0.0,    13500.0,  t.get("TKT-001"), null);
        Purchase pur5  = new Purchase("PUR-005", "2026-04-11", 2, 5000.0,  500.0,  0.0,    9500.0,   t.get("TKT-002"), null);
        Purchase pur6  = new Purchase("PUR-006", "2026-04-14", 4, 3000.0,  600.0,  2850.0, 8550.0,   t.get("TKT-005"), pc.get("GROUPFUN"));
        Purchase pur7  = new Purchase("PUR-007", "2026-04-05", 1, 25000.0, 3750.0, 0.0,    21250.0,  t.get("TKT-004"), null);
        Purchase pur8  = new Purchase("PUR-008", "2026-04-06", 2, 15000.0, 4500.0, 0.0,    25500.0,  t.get("TKT-001"), null);
        Purchase pur9  = new Purchase("PUR-009", "2026-04-13", 1, 3000.0,  150.0,  0.0,    2850.0,   t.get("TKT-005"), null);
        Purchase pur10 = new Purchase("PUR-010", "2026-04-09", 2, 8000.0,  1600.0, 0.0,    14400.0,  t.get("TKT-003"), null);
        Purchase pur11 = new Purchase("PUR-011", "2026-04-10", 1, 5000.0,  500.0,  450.0,  4050.0,   t.get("TKT-002"), pc.get("STUDENT10"));
        Purchase pur12 = new Purchase("PUR-012", "2026-04-16", 5, 5000.0,  1250.0, 0.0,    23750.0,  t.get("TKT-002"), null);
        Purchase pur13 = new Purchase("PUR-013", "2026-04-07", 1, 15000.0, 2250.0, 2550.0, 10200.0,  t.get("TKT-001"), pc.get("EARLY2026"));
        Purchase pur14 = new Purchase("PUR-014", "2026-04-08", 1, 25000.0, 3750.0, 0.0,    21250.0,  t.get("TKT-004"), null);

        purchaseRepository.saveAll(List.of(pur1, pur2, pur3, pur4, pur5, pur6, pur7, pur8, pur9, pur10, pur11, pur12, pur13, pur14));

        Map<String, Purchase> map = new HashMap<>();
        map.put("PUR-001", pur1);   map.put("PUR-002", pur2);   map.put("PUR-003", pur3);
        map.put("PUR-004", pur4);   map.put("PUR-005", pur5);   map.put("PUR-006", pur6);
        map.put("PUR-007", pur7);   map.put("PUR-008", pur8);   map.put("PUR-009", pur9);
        map.put("PUR-010", pur10);  map.put("PUR-011", pur11);  map.put("PUR-012", pur12);
        map.put("PUR-013", pur13);  map.put("PUR-014", pur14);
        return map;
    }

    private void seedCustomers(Map<String, Purchase> p) {
        Customer marko = new Customer("CUS-001", "Marko Petrović", "marko@email.com", CustomerTier.GOLD, List.of(
                new MadePurchase(null, PurchaseMethod.ONLINE, p.get("PUR-001")),
                new MadePurchase(null, PurchaseMethod.MOBILE_APP, p.get("PUR-002"))
        ));

        Customer ana = new Customer("CUS-002", "Ana Jovanović", "ana@email.com", CustomerTier.SILVER, List.of(
                new MadePurchase(null, PurchaseMethod.ONLINE, p.get("PUR-003")),
                new MadePurchase(null, PurchaseMethod.IN_PERSON, p.get("PUR-004"))
        ));

        Customer nikola = new Customer("CUS-003", "Nikola Đorđević", "nikola@email.com", CustomerTier.BRONZE, List.of(
                new MadePurchase(null, PurchaseMethod.MOBILE_APP, p.get("PUR-005")),
                new MadePurchase(null, PurchaseMethod.ONLINE, p.get("PUR-006"))
        ));

        Customer jelena = new Customer("CUS-004", "Jelena Marković", "jelena@email.com", CustomerTier.GOLD, List.of(
                new MadePurchase(null, PurchaseMethod.ONLINE, p.get("PUR-007")),
                new MadePurchase(null, PurchaseMethod.ONLINE, p.get("PUR-008"))
        ));

        Customer stefan = new Customer("CUS-005", "Stefan Ilić", "stefan@email.com", CustomerTier.BRONZE, List.of(
                new MadePurchase(null, PurchaseMethod.IN_PERSON, p.get("PUR-009"))
        ));

        Customer milica = new Customer("CUS-006", "Milica Nikolić", "milica@email.com", CustomerTier.SILVER, List.of(
                new MadePurchase(null, PurchaseMethod.MOBILE_APP, p.get("PUR-010")),
                new MadePurchase(null, PurchaseMethod.ONLINE, p.get("PUR-011"))
        ));

        Customer djordje = new Customer("CUS-007", "Đorđe Stanković", "djordje@email.com", CustomerTier.BRONZE, List.of(
                new MadePurchase(null, PurchaseMethod.IN_PERSON, p.get("PUR-012"))
        ));

        Customer ivana = new Customer("CUS-008", "Ivana Pavlović", "ivana@email.com", CustomerTier.GOLD, List.of(
                new MadePurchase(null, PurchaseMethod.ONLINE, p.get("PUR-013")),
                new MadePurchase(null, PurchaseMethod.ONLINE, p.get("PUR-014"))
        ));

        customerRepository.saveAll(List.of(marko, ana, nikola, jelena, stefan, milica, djordje, ivana));
    }
}
