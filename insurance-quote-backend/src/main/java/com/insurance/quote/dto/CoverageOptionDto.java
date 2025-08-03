package com.insurance.quote.dto;

import com.insurance.quote.entity.CoverageOption;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Data Transfer Object for CoverageOption
 */
public class CoverageOptionDto {

    private Long id;

    @NotBlank(message = "Coverage name is required")
    private String name;

    @NotNull(message = "Coverage type is required")
    private CoverageOption.CoverageType coverageType;

    @NotNull(message = "Premium amount is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Premium must be greater than 0")
    private BigDecimal premium;

    private String description;
    private Boolean isActive = true;
    private Boolean isSelected = false;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructors
    public CoverageOptionDto() {}

    public CoverageOptionDto(String name, CoverageOption.CoverageType coverageType, 
                            BigDecimal premium, String description) {
        this.name = name;
        this.coverageType = coverageType;
        this.premium = premium;
        this.description = description;
        this.isActive = true;
        this.isSelected = false;
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

    public CoverageOption.CoverageType getCoverageType() {
        return coverageType;
    }

    public void setCoverageType(CoverageOption.CoverageType coverageType) {
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
    public String toString() {
        return "CoverageOptionDto{" +
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