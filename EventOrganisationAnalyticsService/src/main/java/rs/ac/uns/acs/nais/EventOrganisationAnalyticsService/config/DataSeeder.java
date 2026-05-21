package rs.ac.uns.acs.nais.EventOrganisationAnalyticsService.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import rs.ac.uns.acs.nais.EventOrganisationAnalyticsService.model.ReservationRequestDocument;
import rs.ac.uns.acs.nais.EventOrganisationAnalyticsService.model.ResourceUsageDocument;
import rs.ac.uns.acs.nais.EventOrganisationAnalyticsService.model.ZahtevaniResursItem;
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

    private static final String[][] BINE = {
            {"bina-1", "Velika bina", "ZATVORENA"},
            {"bina-2", "Mala bina", "ZATVORENA"},
            {"bina-3", "Akustična bina", "ZATVORENA"},
            {"bina-4", "Open Air bina", "OTVORENA"},
            {"bina-5", "Jazz bina", "ZATVORENA"}
    };

    private static final int[] KAPACITETI = {1000, 300, 150, 2000, 200};

    private static final String[] IMENA = {
            "Ana", "Marko", "Jovana", "Stefan", "Milica",
            "Nikola", "Tamara", "Ivan", "Bojana", "Luka",
            "Jelena", "Petar", "Sandra", "Aleksandar", "Katarina",
            "Dragana", "Vladimir", "Nevena", "Miloš", "Tijana"
    };

    private static final String[] PREZIMENA = {
            "Jović", "Petrović", "Nikolić", "Marković", "Đorđević",
            "Stanković", "Ilić", "Popović", "Lazić", "Stojanović",
            "Vasić", "Simić", "Pavlović", "Ristić", "Kovačević",
            "Tadić", "Đukić", "Milošević", "Savić", "Tomić"
    };

    private static final String[] ZANROVI = {
            "Rock", "Pop", "Jazz", "Folk", "Electronic",
            "Classical", "R&B", "Hip-hop", "Metal", "Blues"
    };

    private static final String[] STATUSI = {"CEKANJE", "ODOBREN", "ODBIJEN"};

    private static final String[][] RESURSI = {
            {"resurs-1",  "Ozvučenje",          "AUDIO",   "false"},
            {"resurs-2",  "Bežični mikrofon",    "AUDIO",   "true"},
            {"resurs-3",  "Mixeta",              "AUDIO",   "true"},
            {"resurs-4",  "LED ekran",           "VIDEO",   "false"},
            {"resurs-5",  "Kamera",              "VIDEO",   "true"},
            {"resurs-6",  "Projektor",           "VIDEO",   "false"},
            {"resurs-7",  "Reflektor",           "RASVETA", "true"},
            {"resurs-8",  "Moving head",         "RASVETA", "true"},
            {"resurs-9",  "Par svetlo",          "RASVETA", "true"},
            {"resurs-10", "Dimni top",           "EFEKTI",  "true"},
            {"resurs-11", "Konfeti top",         "EFEKTI",  "true"},
            {"resurs-12", "CO2 efekat",          "EFEKTI",  "true"},
            {"resurs-13", "Subwoofer",           "AUDIO",   "false"},
            {"resurs-14", "Monitor zvučnik",     "AUDIO",   "true"},
            {"resurs-15", "Hazer",               "EFEKTI",  "true"},
            {"resurs-16", "LED traka",           "RASVETA", "true"},
            {"resurs-17", "Studijsko svetlo",    "RASVETA", "false"},
            {"resurs-18", "Stativ za kameru",    "VIDEO",   "true"},
            {"resurs-19", "Žičani mikrofon",     "AUDIO",   "true"},
            {"resurs-20", "DJ kontroler",        "AUDIO",   "true"}
    };

    private static final String[] STATUS_RESURSA = {"DOSTUPAN", "REZERVISAN"};

    private static final String[] RAZLOZI_ODBIJANJA = {
            "Resurs nije dostupan u zadatom periodu",
            "Kapacitet bine ne podržava ovaj resurs",
            "Resurs je rezervisan za drugi event"
    };

    private static final String[] NAPOMENE = {
            "Molimo obezbediti tehničku podršku tokom nastupa",
            "Izvođač dolazi sa sopstvenim instrumentima",
            "Potrebna je garderoba sa ogledalom",
            "Posebni zahtevi u pogledu ozvučenja",
            "Nastup uključuje specijalne vizuelne efekte",
            "Bend ima 5 članova",
            "Gostovanje inostranog izvođača",
            "Festival nastup sa više izvođača",
            "Akustični nastup bez pojačanja",
            null, null
    };

    private static final String[] DETALJI_NASTUPA = {
            "Elektro-pop nastup sa svetlosnim šooom",
            "Akustični recital uz klavir",
            "Rock bend - puna produkcija",
            "Jazz kvartet - intimni nastup",
            "DJ set - elektronska muzika",
            "Folklorna grupa - tradicionalni show",
            "Klasični orkestar - simfonijski koncert",
            "Metal bend - heavy produkcija",
            "R&B solist - soul nastup",
            "Hip-hop kolektiv - live performance",
            "Indie bend - alternativni zvuk",
            "Blues trio - retro atmosfera"
    };

    @Override
    public void run(String... args) {
        seedReservationRequests();
        seedResourceUsage();
    }

    private void seedReservationRequests() {
        if (reservationRequestRepository.count() > 0) {
            log.info("reservation-requests index vec sadrzi podatke, preskacam seed.");
            return;
        }

        log.info("Seeding reservation-requests...");
        List<ReservationRequestDocument> docs = new ArrayList<>();
        LocalDate base = LocalDate.of(2024, 1, 1);

        for (int i = 0; i < 1100; i++) {
            int binaIdx = random.nextInt(5);
            String[] bina = BINE[binaIdx];
            String status = STATUSI[random.nextInt(3)];
            boolean imaTaskove = random.nextInt(3) > 0; // ~67% ima taskove
            int brojTaskova = imaTaskove ? (1 + random.nextInt(7)) : 0;

            LocalDate datumSlanja = base.plusDays(random.nextInt(548));
            LocalDate datumNastupa = datumSlanja.plusDays(5 + random.nextInt(60));
            int pocetakH = 17 + random.nextInt(5);
            int krajH = pocetakH + 1 + random.nextInt(3);

            docs.add(ReservationRequestDocument.builder()
                    .id(UUID.randomUUID().toString())
                    .statusZahteva(status)
                    .datumSlanja(datumSlanja)
                    .datumAzuriranja(datumSlanja.plusDays(random.nextInt(10)))
                    .napomena(pick(NAPOMENE))
                    .binaId(bina[0])
                    .nazivBine(bina[1])
                    .tipBine(bina[2])
                    .kapacitetBine(KAPACITETI[binaIdx])
                    .izvodjacId("izvodjac-" + (random.nextInt(50) + 1))
                    .imeIzvodjaca(pick(IMENA))
                    .prezimeIzvodjaca(pick(PREZIMENA))
                    .zanr(pick(ZANROVI))
                    .popularnost(Math.round((1.0 + random.nextDouble() * 9.0) * 10.0) / 10.0)
                    .datumNastupa(datumNastupa)
                    .vremePocetka(pocetakH)
                    .vremeKraja(krajH)
                    .zahtevanihResursa(buildZahtevanrResursi(imaTaskove))
                    .imaTaskove(imaTaskove)
                    .brojTaskova(brojTaskova)
                    .detaljiNastupa(pick(DETALJI_NASTUPA))
                    .build());
        }

        reservationRequestRepository.saveAll(docs);
        log.info("Upisano {} reservation-requests dokumenata.", docs.size());
    }

    private List<ZahtevaniResursItem> buildZahtevanrResursi(boolean imaTaskove) {
        int count = 1 + random.nextInt(4);
        List<ZahtevaniResursItem> list = new ArrayList<>();
        boolean forcedNonExistentAdded = false;

        for (int i = 0; i < count; i++) {
            String[] resurs = RESURSI[random.nextInt(RESURSI.length)];

            boolean postojiUSistemu;
            if (imaTaskove && !forcedNonExistentAdded && i == count - 1) {
                postojiUSistemu = false;
            } else {
                postojiUSistemu = !imaTaskove || random.nextBoolean();
            }

            if (!postojiUSistemu) {
                forcedNonExistentAdded = true;
            }

            list.add(ZahtevaniResursItem.builder()
                    .nazivResursa(resurs[1])
                    .tipResursa(resurs[2])
                    .zahtevanrKolicina(1 + random.nextInt(5))
                    .postojiUSistemu(postojiUSistemu)
                    .statusResursa(postojiUSistemu ? STATUS_RESURSA[random.nextInt(2)] : "NEDOSTUPAN")
                    .razlogOdbijanja(!postojiUSistemu ? pick(RAZLOZI_ODBIJANJA) : null)
                    .build());
        }

        return list;
    }

    private void seedResourceUsage() {
        if (resourceUsageRepository.count() > 0) {
            log.info("resource-usage index vec sadrzi podatke, preskacam seed.");
            return;
        }

        log.info("Seeding resource-usage...");
        List<ResourceUsageDocument> docs = new ArrayList<>();
        LocalDate base = LocalDate.of(2024, 1, 1);

        for (int i = 0; i < 1200; i++) {
            int binaIdx = random.nextInt(5);
            String[] bina = BINE[binaIdx];
            int resursIdx = random.nextInt(RESURSI.length);
            String[] resurs = RESURSI[resursIdx];

            LocalDate datum = base.plusDays(random.nextInt(548));
            int pocetakH = 17 + random.nextInt(5);
            int krajH = pocetakH + 1 + random.nextInt(3);

            boolean pozajmljeno = random.nextInt(5) == 0; // ~20% pozajmice
            String nazivBinePozajmice = null;
            if (pozajmljeno) {
                int otherIdx = (binaIdx + 1 + random.nextInt(4)) % 5;
                nazivBinePozajmice = BINE[otherIdx][1];
            }

            docs.add(ResourceUsageDocument.builder()
                    .id(UUID.randomUUID().toString())
                    .resursId(resurs[0])
                    .nazivResursa(resurs[1])
                    .tipResursa(resurs[2])
                    .prenosiv(Boolean.parseBoolean(resurs[3]))
                    .dodeljenaKolicina(1 + random.nextInt(15))
                    .binaId(bina[0])
                    .nazivBine(bina[1])
                    .tipBine(bina[2])
                    .terminId("termin-" + (random.nextInt(200) + 1))
                    .datum(datum)
                    .vremePocetka(pocetakH)
                    .vremeKraja(krajH)
                    .pozajmljenoSaBine(pozajmljeno)
                    .nazivBinePozajmice(nazivBinePozajmice)
                    .rezervacijaId("rezervacija-" + (random.nextInt(500) + 1))
                    .build());
        }

        resourceUsageRepository.saveAll(docs);
        log.info("Upisano {} resource-usage dokumenata.", docs.size());
    }

    private <T> T pick(T[] arr) {
        return arr[random.nextInt(arr.length)];
    }
}
