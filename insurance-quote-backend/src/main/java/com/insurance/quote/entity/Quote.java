package com.insurance.quote.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Entity representing an insurance quote
 */
@Entity
@Table(name = "quotes")
public class Quote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Business information is required")
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "business_info_id", nullable = false)
    private BusinessInformation businessInformation;

    @OneToMany(mappedBy = "quote", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<CoverageOption> coverageOptions = new ArrayList<>();

    @NotNull(message = "Total premium is required")
    @DecimalMin(value = "0.0", message = "Total premium must be non-negative")
    @Column(name = "total_premium", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalPremium = BigDecimal.ZERO;

    @Column(name = "risk_rating", length = 50)
    private String riskRating;

    @Column(name = "underwriter_notes", length = 2000)
    private String underwriterNotes;

    @NotNull(message = "Quote status is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private QuoteStatus status = QuoteStatus.DRAFT;

    @Column(name = "quote_number", unique = true, length = 50)
    private String quoteNumber;

    @Column(name = "valid_until")
    private LocalDateTime validUntil;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Quote Status Enum
    public enum QuoteStatus {
        DRAFT("Draft"),
        SAVED("Saved"),
        SUBMITTED("Submitted"),
        APPROVED("Approved"),
        REJECTED("Rejected"),
        EXPIRED("Expired");

        private final String displayName;

        QuoteStatus(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    // Constructors
    public Quote() {}

    public Quote(BusinessInformation businessInformation) {
        this.businessInformation = businessInformation;
        this.status = QuoteStatus.DRAFT;
        this.totalPremium = BigDecimal.ZERO;
    }

    // Business methods
    public void calculateTotalPremium() {
        this.totalPremium = coverageOptions.stream()
                .filter(option -> Boolean.TRUE.equals(option.getIsSelected()))
                .map(CoverageOption::getPremium)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void addCoverageOption(CoverageOption coverageOption) {
        coverageOptions.add(coverageOption);
        coverageOption.setQuote(this);
    }

    public void removeCoverageOption(CoverageOption coverageOption) {
        coverageOptions.remove(coverageOption);
        coverageOption.setQuote(null);
    }

    public boolean isValid() {
        return businessInformation != null && 
               status != null && 
               totalPremium != null &&
               totalPremium.compareTo(BigDecimal.ZERO) >= 0;
    }

    public boolean isExpired() {
        return validUntil != null && LocalDateTime.now().isAfter(validUntil);
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BusinessInformation getBusinessInformation() {
        return businessInformation;
    }

    public void setBusinessInformation(BusinessInformation businessInformation) {
        this.businessInformation = businessInformation;
    }

    public List<CoverageOption> getCoverageOptions() {
        return coverageOptions;
    }

    public void setCoverageOptions(List<CoverageOption> coverageOptions) {
        this.coverageOptions = coverageOptions;
    }

    public BigDecimal getTotalPremium() {
        return totalPremium;
    }

    public void setTotalPremium(BigDecimal totalPremium) {
        this.totalPremium = totalPremium;
    }

    public String getRiskRating() {
        return riskRating;
    }

    public void setRiskRating(String riskRating) {
        this.riskRating = riskRating;
    }

    public String getUnderwriterNotes() {
        return underwriterNotes;
    }

    public void setUnderwriterNotes(String underwriterNotes) {
        this.underwriterNotes = underwriterNotes;
    }

    public QuoteStatus getStatus() {
        return status;
    }

    public void setStatus(QuoteStatus status) {
        this.status = status;
    }

    public String getQuoteNumber() {
        return quoteNumber;
    }

    public void setQuoteNumber(String quoteNumber) {
        this.quoteNumber = quoteNumber;
    }

    public LocalDateTime getValidUntil() {
        return validUntil;
    }

    public void setValidUntil(LocalDateTime validUntil) {
        this.validUntil = validUntil;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Quote quote = (Quote) o;
        return Objects.equals(id, quote.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Quote{" +
                "id=" + id +
                ", totalPremium=" + totalPremium +
                ", riskRating='" + riskRating + '\'' +
                ", status=" + status +
                ", quoteNumber='" + quoteNumber + '\'' +
                ", validUntil=" + validUntil +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}