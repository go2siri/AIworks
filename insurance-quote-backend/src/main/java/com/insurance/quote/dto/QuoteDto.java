package com.insurance.quote.dto;

import com.insurance.quote.entity.Quote;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Transfer Object for Quote
 */
public class QuoteDto {

    private Long id;

    @NotNull(message = "Business information is required")
    @Valid
    private BusinessInformationDto businessInformation;

    @Valid
    private List<CoverageOptionDto> coverageOptions = new ArrayList<>();

    @NotNull(message = "Total premium is required")
    @DecimalMin(value = "0.0", message = "Total premium must be non-negative")
    private BigDecimal totalPremium = BigDecimal.ZERO;

    private String riskRating;
    private String underwriterNotes;

    @NotNull(message = "Quote status is required")
    private Quote.QuoteStatus status = Quote.QuoteStatus.DRAFT;

    private String quoteNumber;
    private LocalDateTime validUntil;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructors
    public QuoteDto() {}

    public QuoteDto(BusinessInformationDto businessInformation) {
        this.businessInformation = businessInformation;
        this.status = Quote.QuoteStatus.DRAFT;
        this.totalPremium = BigDecimal.ZERO;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BusinessInformationDto getBusinessInformation() {
        return businessInformation;
    }

    public void setBusinessInformation(BusinessInformationDto businessInformation) {
        this.businessInformation = businessInformation;
    }

    public List<CoverageOptionDto> getCoverageOptions() {
        return coverageOptions;
    }

    public void setCoverageOptions(List<CoverageOptionDto> coverageOptions) {
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

    public Quote.QuoteStatus getStatus() {
        return status;
    }

    public void setStatus(Quote.QuoteStatus status) {
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
    public String toString() {
        return "QuoteDto{" +
                "id=" + id +
                ", businessInformation=" + businessInformation +
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