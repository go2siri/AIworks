package com.insurance.quote.repository;

import com.insurance.quote.entity.CoverageOption;
import com.insurance.quote.entity.Quote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for CoverageOption entity operations
 */
@Repository
public interface CoverageOptionRepository extends JpaRepository<CoverageOption, Long> {

    /**
     * Find coverage options by quote
     */
    List<CoverageOption> findByQuote(Quote quote);

    /**
     * Find coverage options by quote ID
     */
    List<CoverageOption> findByQuoteId(Long quoteId);

    /**
     * Find coverage options by coverage type
     */
    List<CoverageOption> findByCoverageType(CoverageOption.CoverageType coverageType);

    /**
     * Find active coverage options
     */
    List<CoverageOption> findByIsActiveTrue();

    /**
     * Find inactive coverage options
     */
    List<CoverageOption> findByIsActiveFalse();

    /**
     * Find selected coverage options for a quote
     */
    @Query("SELECT co FROM CoverageOption co WHERE co.quote.id = :quoteId AND co.isSelected = true")
    List<CoverageOption> findSelectedByQuoteId(@Param("quoteId") Long quoteId);

    /**
     * Find unselected coverage options for a quote
     */
    @Query("SELECT co FROM CoverageOption co WHERE co.quote.id = :quoteId AND co.isSelected = false")
    List<CoverageOption> findUnselectedByQuoteId(@Param("quoteId") Long quoteId);

    /**
     * Find coverage options by premium range
     */
    List<CoverageOption> findByPremiumBetween(BigDecimal minPremium, BigDecimal maxPremium);

    /**
     * Find coverage options with premium greater than specified amount
     */
    List<CoverageOption> findByPremiumGreaterThan(BigDecimal minPremium);

    /**
     * Find coverage options with premium less than specified amount
     */
    List<CoverageOption> findByPremiumLessThan(BigDecimal maxPremium);

    /**
     * Find coverage option by name and coverage type
     */
    Optional<CoverageOption> findByNameAndCoverageType(String name, CoverageOption.CoverageType coverageType);

    /**
     * Calculate total premium for selected coverage options of a quote
     */
    @Query("SELECT COALESCE(SUM(co.premium), 0) FROM CoverageOption co WHERE co.quote.id = :quoteId AND co.isSelected = true")
    BigDecimal calculateTotalPremiumForQuote(@Param("quoteId") Long quoteId);

    /**
     * Count selected coverage options for a quote
     */
    @Query("SELECT COUNT(co) FROM CoverageOption co WHERE co.quote.id = :quoteId AND co.isSelected = true")
    long countSelectedByQuoteId(@Param("quoteId") Long quoteId);

    /**
     * Count coverage options by type
     */
    long countByCoverageType(CoverageOption.CoverageType coverageType);

    /**
     * Find coverage options by quote and active status
     */
    List<CoverageOption> findByQuoteAndIsActive(Quote quote, Boolean isActive);

    /**
     * Find most expensive coverage option
     */
    @Query("SELECT co FROM CoverageOption co WHERE co.premium = (SELECT MAX(c.premium) FROM CoverageOption c)")
    List<CoverageOption> findMostExpensiveCoverageOptions();

    /**
     * Find least expensive coverage option
     */
    @Query("SELECT co FROM CoverageOption co WHERE co.premium = (SELECT MIN(c.premium) FROM CoverageOption c)")
    List<CoverageOption> findLeastExpensiveCoverageOptions();

    /**
     * Find coverage options by name containing text (case-insensitive)
     */
    List<CoverageOption> findByNameContainingIgnoreCase(String namePattern);

    /**
     * Check if coverage option exists for a quote and coverage type
     */
    boolean existsByQuoteAndCoverageType(Quote quote, CoverageOption.CoverageType coverageType);

    /**
     * Delete coverage options by quote
     */
    void deleteByQuote(Quote quote);
}