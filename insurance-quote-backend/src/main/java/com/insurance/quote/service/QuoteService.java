package com.insurance.quote.service;

import com.insurance.quote.dto.QuoteDto;
import com.insurance.quote.entity.Quote;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service interface for Quote operations
 */
public interface QuoteService {

    /**
     * Create a new quote
     */
    QuoteDto createQuote(QuoteDto quoteDto);

    /**
     * Update an existing quote
     */
    QuoteDto updateQuote(Long id, QuoteDto quoteDto);

    /**
     * Get quote by ID
     */
    Optional<QuoteDto> getQuoteById(Long id);

    /**
     * Get quote by quote number
     */
    Optional<QuoteDto> getQuoteByNumber(String quoteNumber);

    /**
     * Get all quotes with pagination
     */
    Page<QuoteDto> getAllQuotes(Pageable pageable);

    /**
     * Get quotes by status
     */
    List<QuoteDto> getQuotesByStatus(Quote.QuoteStatus status);

    /**
     * Search quotes by business name
     */
    Page<QuoteDto> searchQuotesByBusinessName(String businessName, Pageable pageable);

    /**
     * Get quotes by state
     */
    List<QuoteDto> getQuotesByState(String state);

    /**
     * Get quotes created between dates
     */
    List<QuoteDto> getQuotesCreatedBetween(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Get expired quotes
     */
    List<QuoteDto> getExpiredQuotes();

    /**
     * Delete quote by ID
     */
    void deleteQuote(Long id);

    /**
     * Calculate quote premium based on selected coverage options
     */
    BigDecimal calculateQuotePremium(Long quoteId);

    /**
     * Submit quote for approval
     */
    QuoteDto submitQuote(Long id);

    /**
     * Approve quote
     */
    QuoteDto approveQuote(Long id);

    /**
     * Reject quote
     */
    QuoteDto rejectQuote(Long id, String reason);

    /**
     * Check if quote number is unique
     */
    boolean isQuoteNumberUnique(String quoteNumber);

    /**
     * Generate unique quote number
     */
    String generateQuoteNumber();

    /**
     * Get quote statistics
     */
    QuoteStatistics getQuoteStatistics();

    /**
     * Inner class for quote statistics
     */
    class QuoteStatistics {
        private long totalQuotes;
        private long draftQuotes;
        private long savedQuotes;
        private long submittedQuotes;
        private long approvedQuotes;
        private long rejectedQuotes;
        private long expiredQuotes;
        private BigDecimal totalPremiumValue;
        private BigDecimal averagePremium;

        // Constructors, getters, and setters
        public QuoteStatistics() {}

        public QuoteStatistics(long totalQuotes, long draftQuotes, long savedQuotes, 
                              long submittedQuotes, long approvedQuotes, long rejectedQuotes, 
                              long expiredQuotes, BigDecimal totalPremiumValue, BigDecimal averagePremium) {
            this.totalQuotes = totalQuotes;
            this.draftQuotes = draftQuotes;
            this.savedQuotes = savedQuotes;
            this.submittedQuotes = submittedQuotes;
            this.approvedQuotes = approvedQuotes;
            this.rejectedQuotes = rejectedQuotes;
            this.expiredQuotes = expiredQuotes;
            this.totalPremiumValue = totalPremiumValue;
            this.averagePremium = averagePremium;
        }

        // Getters and Setters
        public long getTotalQuotes() { return totalQuotes; }
        public void setTotalQuotes(long totalQuotes) { this.totalQuotes = totalQuotes; }

        public long getDraftQuotes() { return draftQuotes; }
        public void setDraftQuotes(long draftQuotes) { this.draftQuotes = draftQuotes; }

        public long getSavedQuotes() { return savedQuotes; }
        public void setSavedQuotes(long savedQuotes) { this.savedQuotes = savedQuotes; }

        public long getSubmittedQuotes() { return submittedQuotes; }
        public void setSubmittedQuotes(long submittedQuotes) { this.submittedQuotes = submittedQuotes; }

        public long getApprovedQuotes() { return approvedQuotes; }
        public void setApprovedQuotes(long approvedQuotes) { this.approvedQuotes = approvedQuotes; }

        public long getRejectedQuotes() { return rejectedQuotes; }
        public void setRejectedQuotes(long rejectedQuotes) { this.rejectedQuotes = rejectedQuotes; }

        public long getExpiredQuotes() { return expiredQuotes; }
        public void setExpiredQuotes(long expiredQuotes) { this.expiredQuotes = expiredQuotes; }

        public BigDecimal getTotalPremiumValue() { return totalPremiumValue; }
        public void setTotalPremiumValue(BigDecimal totalPremiumValue) { this.totalPremiumValue = totalPremiumValue; }

        public BigDecimal getAveragePremium() { return averagePremium; }
        public void setAveragePremium(BigDecimal averagePremium) { this.averagePremium = averagePremium; }

        @Override
        public String toString() {
            return "QuoteStatistics{" +
                    "totalQuotes=" + totalQuotes +
                    ", draftQuotes=" + draftQuotes +
                    ", savedQuotes=" + savedQuotes +
                    ", submittedQuotes=" + submittedQuotes +
                    ", approvedQuotes=" + approvedQuotes +
                    ", rejectedQuotes=" + rejectedQuotes +
                    ", expiredQuotes=" + expiredQuotes +
                    ", totalPremiumValue=" + totalPremiumValue +
                    ", averagePremium=" + averagePremium +
                    '}';
        }
    }
}