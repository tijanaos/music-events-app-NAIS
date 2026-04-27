package rs.ac.uns.acs.nais.GraphDatabaseService.repository;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.stereotype.Repository;
import rs.ac.uns.acs.nais.GraphDatabaseService.dto.query.GenreReservationStats;
import rs.ac.uns.acs.nais.GraphDatabaseService.dto.query.PerformerBookingStats;
import rs.ac.uns.acs.nais.GraphDatabaseService.dto.query.ReservationMissingResource;
import rs.ac.uns.acs.nais.GraphDatabaseService.model.Reservation;
import rs.ac.uns.acs.nais.GraphDatabaseService.model.enums.ReservationStatus;

import java.util.List;

@Repository
public interface ReservationRepository extends Neo4jRepository<Reservation, String> {

    List<Reservation> findByStatus(ReservationStatus status);

    List<Reservation> findByCreatedBy(String createdBy);

    // Query 2: For each performer with approved reservations, count bookings and calculate average fee
    @Query("MATCH (r:Reservation)-[fp:FOR_PERFORMER]->(p:Performer) " +
           "WHERE r.status = 'APPROVED' " +
           "WITH p, count(r) AS bookingCount, avg(fp.agreedFee) AS averageFee " +
           "RETURN p.id AS performerId, p.name AS performerName, p.genre AS genre, " +
           "bookingCount, averageFee " +
           "ORDER BY bookingCount DESC")
    List<PerformerBookingStats> findPerformerBookingStats();

    // Query 3: Reservation count and average fee grouped by genre, with average popularity
    @Query("MATCH (r:Reservation)-[fp:FOR_PERFORMER]->(p:Performer) " +
           "WITH p.genre AS genre, count(r) AS reservationCount, " +
           "avg(fp.agreedFee) AS averageFee, avg(p.popularity) AS avgPopularity " +
           "WHERE reservationCount > 0 " +
           "RETURN genre, reservationCount, averageFee, avgPopularity " +
           "ORDER BY reservationCount DESC")
    List<GenreReservationStats> findGenreReservationStats();

    // Query 4: For each reservation, count missing resources and total requested quantity
    @Query("MATCH (r:Reservation)-[rr:REQUIRES_RESOURCE]->(res:Resource) " +
           "WHERE rr.existsInSystem = false " +
           "WITH r, count(res) AS missingCount, sum(rr.requestedQuantity) AS totalRequestedQuantity " +
           "RETURN r.id AS reservationId, r.status AS reservationStatus, " +
           "r.createdBy AS createdBy, missingCount, totalRequestedQuantity " +
           "ORDER BY missingCount DESC")
    List<ReservationMissingResource> findReservationsWithMissingResources();
}
