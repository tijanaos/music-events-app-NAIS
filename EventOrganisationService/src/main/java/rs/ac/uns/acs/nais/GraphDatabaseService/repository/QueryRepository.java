package rs.ac.uns.acs.nais.GraphDatabaseService.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import rs.ac.uns.acs.nais.GraphDatabaseService.dto.query.GenreReservationStats;
import rs.ac.uns.acs.nais.GraphDatabaseService.dto.query.PerformerBookingStats;
import rs.ac.uns.acs.nais.GraphDatabaseService.dto.query.ResourceApprovalResult;
import rs.ac.uns.acs.nais.GraphDatabaseService.dto.query.StageConfirmationResult;
import rs.ac.uns.acs.nais.GraphDatabaseService.dto.query.StageResourceSummary;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class QueryRepository {

    private final Neo4jClient neo4jClient;

    public List<StageResourceSummary> getStageResourceSummary() {
        return neo4jClient.query(
                "MATCH (s:Stage)-[r:HAS_RESOURCE]->(res:Resource) " +
                "WHERE s.active = true " +
                "WITH s, count(res) AS totalResources, sum(r.availableQuantity) AS totalAvailableQuantity " +
                "RETURN s.id AS stageId, s.name AS stageName, s.type AS stageType, " +
                "totalResources, totalAvailableQuantity " +
                "ORDER BY totalResources DESC")
                .fetchAs(StageResourceSummary.class)
                .mappedBy((typeSystem, record) -> new StageResourceSummary(
                        record.get("stageId").asString(),
                        record.get("stageName").asString(),
                        record.get("stageType").asString(),
                        record.get("totalResources").asLong(),
                        record.get("totalAvailableQuantity").asLong()
                ))
                .all()
                .stream().toList();
    }

    public List<PerformerBookingStats> getPerformerBookingStats() {
        return neo4jClient.query(
                "MATCH (r:Reservation)-[fp:FOR_PERFORMER]->(p:Performer) " +
                "WHERE r.status = 'APPROVED' " +
                "WITH p, count(r) AS bookingCount, avg(fp.agreedFee) AS averageFee " +
                "RETURN p.id AS performerId, p.name AS performerName, p.genre AS genre, " +
                "bookingCount, averageFee " +
                "ORDER BY bookingCount DESC")
                .fetchAs(PerformerBookingStats.class)
                .mappedBy((typeSystem, record) -> new PerformerBookingStats(
                        record.get("performerId").asString(),
                        record.get("performerName").asString(),
                        record.get("genre").asString(),
                        record.get("bookingCount").asLong(),
                        record.get("averageFee").asDouble()
                ))
                .all()
                .stream().toList();
    }

    public List<GenreReservationStats> getGenreReservationStats() {
        return neo4jClient.query(
                "MATCH (r:Reservation)-[fp:FOR_PERFORMER]->(p:Performer) " +
                "WITH p.genre AS genre, count(r) AS reservationCount, " +
                "avg(fp.agreedFee) AS averageFee, avg(p.popularity) AS avgPopularity " +
                "WHERE reservationCount > 0 " +
                "RETURN genre, reservationCount, averageFee, avgPopularity " +
                "ORDER BY reservationCount DESC")
                .fetchAs(GenreReservationStats.class)
                .mappedBy((typeSystem, record) -> new GenreReservationStats(
                        record.get("genre").asString(),
                        record.get("reservationCount").asLong(),
                        record.get("averageFee").asDouble(),
                        record.get("avgPopularity").asDouble()
                ))
                .all()
                .stream().toList();
    }

    @Transactional
    public List<StageConfirmationResult> confirmStageForApprovedReservations() {
        return neo4jClient.query(
                "MATCH (r:Reservation)-[os:ON_STAGE]->(s:Stage) " +
                "WHERE r.status = 'APPROVED' AND os.confirmed = false " +
                "SET os.confirmed = true " +
                "RETURN r.id AS reservationId, s.name AS stageName, os.confirmed AS confirmed")
                .fetchAs(StageConfirmationResult.class)
                .mappedBy((typeSystem, record) -> new StageConfirmationResult(
                        record.get("reservationId").asString(),
                        record.get("stageName").asString(),
                        record.get("confirmed").asBoolean()
                ))
                .all()
                .stream().toList();
    }

    @Transactional
    public List<ResourceApprovalResult> approveExistingResourceRequests() {
        return neo4jClient.query(
                "MATCH (r:Reservation)-[rr:REQUIRES_RESOURCE]->(res:Resource) " +
                "WHERE r.status = 'APPROVED' AND rr.existsInSystem = true AND rr.status = 'PENDING' " +
                "SET rr.status = 'APPROVED' " +
                "RETURN r.id AS reservationId, res.name AS resourceName, rr.status AS updatedStatus")
                .fetchAs(ResourceApprovalResult.class)
                .mappedBy((typeSystem, record) -> new ResourceApprovalResult(
                        record.get("reservationId").asString(),
                        record.get("resourceName").asString(),
                        record.get("updatedStatus").asString()
                ))
                .all()
                .stream().toList();
    }
}
