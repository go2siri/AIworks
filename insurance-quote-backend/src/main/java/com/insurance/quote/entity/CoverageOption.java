package com.insurance.quote.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entity representing coverage options for insurance quotes
 */
@Entity
@Table(name = "coverage_options")
public class CoverageOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Coverage name is required")
    @Column(name = "name", nullable = false)
    private String name;

    @NotNull(message = "Coverage type is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "coverage_type", nullable = false)
    private CoverageType coverageType;

    @NotNull(message = "Premium amount is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Premium must be greater than 0")
    @Column(name = "premium", nullable = false, precision = 10, scale = 2)
    private BigDecimal premium;

    @Column(name = "description", length = 1000)
    private String description;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quote_id")
    private Quote quote;

    @Column(name = "is_selected", nullable = false)
    private Boolean isSelected = false;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Constructors
    public CoverageOption() {}

    public CoverageOption(String name, CoverageType coverageType, BigDecimal premium, String description) {
        this.name = name;
        this.coverageType = coverageType;
        this.premium = premium;
        this.description = description;
        this.isActive = true;
        this.isSelected = false;
    }

    // Coverage Type Enum
    public enum CoverageType {
        GENERAL_LIABILITY("General Liability", "Protects against claims of bodily injury, property damage, and personal injury"),
        PROPERTY("Property Insurance", "Covers business property including buildings, equipment, inventory, and furniture"),
        ADDITIONAL("Additional Coverage", "Includes cyber liability, employment practices liability, and other specialized coverages");

        private final String displayName;
        private final String description;

        CoverageType(String displayName, String description) {
            this.displayName = displayName;
            this.description = description;
        }

        public String getDisplayName() {
            return displayName;
        }

        public String getDescription() {
            return description;
        }
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public CoverageType getCoverageType() {
        return coverageType;
    }

    public void setCoverageType(CoverageType coverageType) {
        this.coverageType = coverageType;
    }

    public BigDecimal getPremium() {
        return premium;
    }

    public void setPremium(BigDecimal premium) {
        this.premium = premium;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public Quote getQuote() {
        return quote;
    }

    public void setQuote(Quote quote) {
        this.quote = quote;
    }

    public Boolean getIsSelected() {
        return isSelected;
    }

    public void setIsSelected(Boolean isSelected) {
        this.isSelected = isSelected;
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
        CoverageOption that = (CoverageOption) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "CoverageOption{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", coverageType=" + coverageType +
                ", premium=" + premium +
                ", description='" + description + '\'' +
                ", isActive=" + isActive +
                ", isSelected=" + isSelected +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}