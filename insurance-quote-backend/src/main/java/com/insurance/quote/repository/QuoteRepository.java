package com.insurance.quote.repository;

import com.insurance.quote.entity.Quote;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Quote entity operations
 */
@Repository
public interface QuoteRepository extends JpaRepository<Quote, Long> {

    /**
     * Find quote by quote number
     */
    Optional<Quote> findByQuoteNumber(String quoteNumber);

    /**
     * Find quotes by status
     */
    List<Quote> findByStatus(Quote.QuoteStatus status);

    /**
     * Find quotes by business name (case-insensitive)
     */
    @Query("SELECT q FROM Quote q WHERE LOWER(q.businessInformation.name) LIKE LOWER(CONCAT('%', :businessName, '%'))")
    Page<Quote> findByBusinessNameContainingIgnoreCase(@Param("businessName") String businessName, Pageable pageable);

    /**
     * Find quotes by state
     */
    @Query("SELECT q FROM Quote q WHERE q.businessInformation.state = :state")
    List<Quote> findByState(@Param("state") String state);

    /**
     * Find quotes created between dates
     */
    List<Quote> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Find expired quotes
     */
    @Query("SELECT q FROM Quote q WHERE q.validUntil IS NOT NULL AND q.validUntil < :currentDateTime")
    List<Quote> findExpiredQuotes(@Param("currentDateTime") LocalDateTime currentDateTime);

    /**
     * Find quotes by business type
     */
    @Query("SELECT q FROM Quote q WHERE q.businessInformation.businessType = :businessType")
    List<Quote> findByBusinessType(@Param("businessType") com.insurance.quote.entity.BusinessInformation.BusinessType businessType);

    /**
     * Find quotes by industry
     */
    @Query("SELECT q FROM Quote q WHERE q.businessInformation.industry = :industry")
    List<Quote> findByIndustry(@Param("industry") com.insurance.quote.entity.BusinessInformation.Industry industry);

    /**
     * Count quotes by status
     */
    long countByStatus(Quote.QuoteStatus status);

    /**
     * Find quotes with total premium greater than specified amount
     */
    @Query("SELECT q FROM Quote q WHERE q.totalPremium > :minPremium")
    List<Quote> findByTotalPremiumGreaterThan(@Param("minPremium") java.math.BigDecimal minPremium);

    /**
     * Find quotes created today
     */
    @Query("SELECT q FROM Quote q WHERE q.createdAt >= :startOfDay AND q.createdAt < :startOfNextDay")
    List<Quote> findQuotesCreatedToday(@Param("startOfDay") LocalDateTime startOfDay, @Param("startOfNextDay") LocalDateTime startOfNextDay);

    /**
     * Find quotes that need attention (draft for more than specified days)
     */
    @Query("SELECT q FROM Quote q WHERE q.status = com.insurance.quote.entity.Quote$QuoteStatus.DRAFT AND q.createdAt < :cutoffDate")
    List<Quote> findStaleQuotes(@Param("cutoffDate") LocalDateTime cutoffDate);

    /**
     * Check if quote number exists
     */
    boolean existsByQuoteNumber(String quoteNumber);
}