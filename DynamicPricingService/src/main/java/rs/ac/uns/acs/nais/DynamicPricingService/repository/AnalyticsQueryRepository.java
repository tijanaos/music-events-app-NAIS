package rs.ac.uns.acs.nais.DynamicPricingService.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.stereotype.Repository;
import rs.ac.uns.acs.nais.DynamicPricingService.dto.response.analytics.*;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class AnalyticsQueryRepository {

    private final Neo4jClient neo4jClient;

    public List<TicketRecommendationResponse> findTicketRecommendations(String customerId, String today) {
        return neo4jClient.query("""
                        MATCH (target:Customer {id: $customerId})
                              -[:MADE_PURCHASE]->(:Purchase)-[:FOR_TICKET]->(bought:TicketType)
                        WITH target, COLLECT(DISTINCT bought.id) AS boughtIds
                        MATCH (other:Customer)-[:MADE_PURCHASE]->(:Purchase)-[:FOR_TICKET]->(shared:TicketType)
                        WHERE other <> target
                          AND shared.id IN boughtIds
                        WITH target, boughtIds, other, COUNT(DISTINCT shared) AS overlap
                        MATCH (other)-[:MADE_PURCHASE]->(:Purchase)-[:FOR_TICKET]->(rec:TicketType)
                        WHERE NOT rec.id IN boughtIds
                        WITH rec, COUNT(DISTINCT other) AS recommendedBy, MAX(overlap) AS maxOverlap
                        OPTIONAL MATCH (rec)-[:HAS_SCHEDULE]->(s:PriceSchedule)
                        WHERE s.periodStart <= $today AND $today <= s.periodEnd
                        RETURN rec.id          AS ticketTypeId,
                               rec.name        AS name,
                               s.currentPrice  AS currentPrice,
                               recommendedBy,
                               maxOverlap
                        ORDER BY recommendedBy DESC, maxOverlap DESC
                        """)
                .bind(customerId).to("customerId")
                .bind(today).to("today")
                .fetchAs(TicketRecommendationResponse.class)
                .mappedBy((typeSystem, record) -> new TicketRecommendationResponse(
                        record.get("ticketTypeId").asString(null),
                        record.get("name").asString(null),
                        record.get("currentPrice").isNull() ? null : record.get("currentPrice").asDouble(),
                        record.get("recommendedBy").asLong(),
                        record.get("maxOverlap").asLong()
                ))
                .all().stream().toList();
    }

    public List<TicketPricingRecommendationResponse> findPricingRecommendations(String today, String windowStart) {
        return neo4jClient.query("""
                        MATCH (t:TicketType)-[:HAS_SCHEDULE]->(s:PriceSchedule)
                        WHERE s.periodStart <= $today AND $today <= s.periodEnd
                        OPTIONAL MATCH (p:Purchase)-[:FOR_TICKET]->(t)
                        WHERE p.date >= $windowStart AND p.date <= $today
                        WITH t, s, COUNT(p) AS recentSales,
                             toFloat(t.soldCount) / toFloat(t.maxAvailable) AS capacityRatio
                        WITH t, s, recentSales, capacityRatio,
                             toFloat(recentSales) / toFloat(s.expectedSales) AS sellThrough,
                             CASE
                               WHEN capacityRatio >= 0.9  THEN 'CRITICAL_CAPACITY'
                               WHEN toFloat(recentSales) / toFloat(s.expectedSales) > 1.2 THEN 'INCREASE'
                               WHEN toFloat(recentSales) / toFloat(s.expectedSales) < 0.8 THEN 'DECREASE'
                               ELSE 'HOLD'
                             END AS action
                        RETURN t.id            AS ticketTypeId,
                               t.name          AS name,
                               s.currentPrice  AS currentPrice,
                               s.basePrice     AS basePrice,
                               s.minPrice      AS minPrice,
                               s.priceStep     AS priceStep,
                               t.soldCount     AS soldCount,
                               t.maxAvailable  AS maxAvailable,
                               recentSales,
                               s.expectedSales AS expectedSales,
                               sellThrough,
                               capacityRatio,
                               action
                        ORDER BY
                          CASE action
                            WHEN 'CRITICAL_CAPACITY' THEN 1
                            WHEN 'INCREASE'          THEN 2
                            WHEN 'HOLD'              THEN 3
                            ELSE 4
                          END ASC,
                          sellThrough DESC
                        """)
                .bind(today).to("today")
                .bind(windowStart).to("windowStart")
                .fetchAs(TicketPricingRecommendationResponse.class)
                .mappedBy((typeSystem, record) -> new TicketPricingRecommendationResponse(
                        record.get("ticketTypeId").asString(null),
                        record.get("name").asString(null),
                        record.get("currentPrice").isNull() ? null : record.get("currentPrice").asDouble(),
                        record.get("basePrice").isNull() ? null : record.get("basePrice").asDouble(),
                        record.get("minPrice").isNull() ? null : record.get("minPrice").asDouble(),
                        record.get("priceStep").isNull() ? null : record.get("priceStep").asDouble(),
                        record.get("soldCount").asLong(),
                        record.get("maxAvailable").asLong(),
                        record.get("recentSales").asLong(),
                        record.get("expectedSales").asLong(),
                        record.get("sellThrough").asDouble(),
                        record.get("capacityRatio").asDouble(),
                        record.get("action").asString(null)
                ))
                .all().stream().toList();
    }

    public List<PromoCodeEffectivenessResponse> findPromoCodeEffectiveness() {
        return neo4jClient.query("""
                        MATCH (pc:PromoCode)
                        OPTIONAL MATCH (p:Purchase)-[:USED_PROMO]->(pc)
                        WITH pc,
                             COUNT(DISTINCT p)                          AS usageCount,
                             COALESCE(SUM(p.promoDiscountApplied), 0.0) AS totalDiscount
                        OPTIONAL MATCH (c:Customer)-[:MADE_PURCHASE]->(:Purchase)-[:USED_PROMO]->(pc)
                        WITH pc, usageCount, totalDiscount,
                             COUNT(DISTINCT c) AS uniqueBuyers
                        OPTIONAL MATCH (pc)-[:VALID_FOR]->(tt:TicketType)
                        WITH pc, usageCount, totalDiscount, uniqueBuyers,
                             COLLECT(DISTINCT tt.name) AS validForTickets
                        RETURN pc.id              AS promoCodeId,
                               pc.code            AS code,
                               pc.discountPercent AS discountPercent,
                               pc.validFrom       AS validFrom,
                               pc.validTo         AS validTo,
                               pc.maxUses         AS maxUses,
                               usageCount,
                               uniqueBuyers,
                               totalDiscount,
                               CASE WHEN pc.maxUses IS NOT NULL
                                    THEN toFloat(usageCount) / toFloat(pc.maxUses)
                                    ELSE null END  AS utilizationRate,
                               validForTickets
                        ORDER BY totalDiscount DESC
                        """)
                .fetchAs(PromoCodeEffectivenessResponse.class)
                .mappedBy((typeSystem, record) -> new PromoCodeEffectivenessResponse(
                        record.get("promoCodeId").asString(null),
                        record.get("code").asString(null),
                        record.get("discountPercent").isNull() ? null : record.get("discountPercent").asLong(),
                        record.get("validFrom").asString(null),
                        record.get("validTo").asString(null),
                        record.get("maxUses").isNull() ? null : record.get("maxUses").asLong(),
                        record.get("usageCount").asLong(),
                        record.get("uniqueBuyers").asLong(),
                        record.get("totalDiscount").asDouble(),
                        record.get("utilizationRate").isNull() ? null : record.get("utilizationRate").asDouble(),
                        record.get("validForTickets").asList(v -> v.asString())
                ))
                .all().stream().toList();
    }

    public List<CustomerSpendingResponse> findCustomerSpendingProfiles() {
        return neo4jClient.query("""
                        MATCH (c:Customer)-[:MADE_PURCHASE]->(p:Purchase)
                        OPTIONAL MATCH (p)-[:USED_PROMO]->(pc:PromoCode)
                        OPTIONAL MATCH (p)-[:FOR_TICKET]->(t:TicketType)
                        WITH c,
                             COUNT(DISTINCT p)           AS purchaseCount,
                             SUM(p.finalPrice)           AS totalSpent,
                             SUM(p.tierDiscountApplied)  AS totalTierDiscount,
                             SUM(p.promoDiscountApplied) AS totalPromoDiscount,
                             COUNT(DISTINCT pc)          AS promosUsed,
                             COLLECT(DISTINCT t.name)    AS ticketTypesBought
                        WITH c, purchaseCount, totalSpent, promosUsed, ticketTypesBought,
                             totalTierDiscount + totalPromoDiscount AS totalDiscount
                        RETURN c.id              AS customerId,
                               c.name            AS name,
                               c.tier            AS tier,
                               purchaseCount,
                               totalSpent,
                               totalDiscount,
                               toFloat(totalDiscount) / toFloat(totalSpent + totalDiscount) AS effectiveDiscountRate,
                               promosUsed,
                               ticketTypesBought
                        ORDER BY totalSpent DESC
                        """)
                .fetchAs(CustomerSpendingResponse.class)
                .mappedBy((typeSystem, record) -> new CustomerSpendingResponse(
                        record.get("customerId").asString(null),
                        record.get("name").asString(null),
                        record.get("tier").asString(null),
                        record.get("purchaseCount").asLong(),
                        record.get("totalSpent").asDouble(),
                        record.get("totalDiscount").asDouble(),
                        record.get("effectiveDiscountRate").asDouble(),
                        record.get("promosUsed").asLong(),
                        record.get("ticketTypesBought").asList(v -> v.asString())
                ))
                .all().stream().toList();
    }

    public List<TierUpgradeResponse> upgradeTiers(double silverThreshold, double goldThreshold) {
        return neo4jClient.query("""
                        MATCH (c:Customer)-[:MADE_PURCHASE]->(p:Purchase)
                        WITH c, SUM(p.finalPrice) AS totalSpent
                        WHERE c.tier <> 'GOLD'
                        WITH c, totalSpent, c.tier AS oldTier,
                             CASE
                               WHEN totalSpent >= $goldThreshold   THEN 'GOLD'
                               WHEN totalSpent >= $silverThreshold THEN 'SILVER'
                               ELSE c.tier
                             END AS newTier
                        WHERE oldTier <> newTier
                        SET c.tier = newTier
                        RETURN c.id AS customerId,
                               c.name AS name,
                               oldTier,
                               newTier,
                               totalSpent
                        ORDER BY totalSpent DESC
                        """)
                .bind(silverThreshold).to("silverThreshold")
                .bind(goldThreshold).to("goldThreshold")
                .fetchAs(TierUpgradeResponse.class)
                .mappedBy((typeSystem, record) -> new TierUpgradeResponse(
                        record.get("customerId").asString(null),
                        record.get("name").asString(null),
                        record.get("oldTier").asString(null),
                        record.get("newTier").asString(null),
                        record.get("totalSpent").asDouble()
                ))
                .all().stream().toList();
    }
}
