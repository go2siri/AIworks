package com.insurance.quote.dto;

import com.insurance.quote.entity.BusinessInformation;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for BusinessInformation
 */
public class BusinessInformationDto {

    private Long id;

    @NotBlank(message = "Business name is required")
    @Size(min = 2, max = 255, message = "Business name must be between 2 and 255 characters")
    private String name;

    @NotNull(message = "Business type is required")
    private BusinessInformation.BusinessType businessType;

    @NotNull(message = "Industry is required")
    private BusinessInformation.Industry industry;

    @NotBlank(message = "State is required")
    @Pattern(regexp = "^[A-Z]{2}$", message = "State must be a 2-letter uppercase code")
    private String state;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructors
    public BusinessInformationDto() {}

    public BusinessInformationDto(String name, BusinessInformation.BusinessType businessType, 
                                 BusinessInformation.Industry industry, String state) {
        this.name = name;
        this.businessType = businessType;
        this.industry = industry;
        this.state = state;
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

    public BusinessInformation.BusinessType getBusinessType() {
        return businessType;
    }

    public void setBusinessType(BusinessInformation.BusinessType businessType) {
        this.businessType = businessType;
    }

    public BusinessInformation.Industry getIndustry() {
        return industry;
    }

    public void setIndustry(BusinessInformation.Industry industry) {
        this.industry = industry;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
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
        return "BusinessInformationDto{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", businessType=" + businessType +
                ", industry=" + industry +
                ", state='" + state + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}