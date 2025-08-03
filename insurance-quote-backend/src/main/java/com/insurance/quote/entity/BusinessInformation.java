package com.insurance.quote.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entity representing business information for insurance quotes
 */
@Entity
@Table(name = "business_information")
public class BusinessInformation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Business name is required")
    @Size(min = 2, max = 255, message = "Business name must be between 2 and 255 characters")
    @Column(name = "name", nullable = false)
    private String name;

    @NotNull(message = "Business type is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "business_type", nullable = false)
    private BusinessType businessType;

    @NotNull(message = "Industry is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "industry", nullable = false)
    private Industry industry;

    @NotBlank(message = "State is required")
    @Pattern(regexp = "^[A-Z]{2}$", message = "State must be a 2-letter uppercase code")
    @Column(name = "state", nullable = false, length = 2)
    private String state;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Constructors
    public BusinessInformation() {}

    public BusinessInformation(String name, BusinessType businessType, Industry industry, String state) {
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

    public BusinessType getBusinessType() {
        return businessType;
    }

    public void setBusinessType(BusinessType businessType) {
        this.businessType = businessType;
    }

    public Industry getIndustry() {
        return industry;
    }

    public void setIndustry(Industry industry) {
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

    // Business Type Enum
    public enum BusinessType {
        RETAIL("Retail"),
        RESTAURANT("Restaurant"),
        TECHNOLOGY("Technology"),
        MANUFACTURING("Manufacturing"),
        HEALTHCARE("Healthcare"),
        PROFESSIONAL("Professional Services");

        private final String displayName;

        BusinessType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    // Industry Enum
    public enum Industry {
        FOOD_SERVICE("Food Service"),
        RETAIL_TRADE("Retail Trade"),
        SOFTWARE("Software Development"),
        HEALTHCARE_SERVICES("Healthcare Services"),
        CONSULTING("Consulting"),
        MANUFACTURING("Manufacturing");

        private final String displayName;

        Industry(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BusinessInformation that = (BusinessInformation) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "BusinessInformation{" +
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