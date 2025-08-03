package com.insurance.quote.repository;

import com.insurance.quote.entity.BusinessInformation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for BusinessInformation entity operations
 */
@Repository
public interface BusinessInformationRepository extends JpaRepository<BusinessInformation, Long> {

    /**
     * Find business information by exact name (case-insensitive)
     */
    Optional<BusinessInformation> findByNameIgnoreCase(String name);

    /**
     * Find business information by name containing specified text
     */
    List<BusinessInformation> findByNameContainingIgnoreCase(String namePattern);

    /**
     * Find business information by business type
     */
    List<BusinessInformation> findByBusinessType(BusinessInformation.BusinessType businessType);

    /**
     * Find business information by industry
     */
    List<BusinessInformation> findByIndustry(BusinessInformation.Industry industry);

    /**
     * Find business information by state
     */
    List<BusinessInformation> findByState(String state);

    /**
     * Find business information by business type and industry
     */
    List<BusinessInformation> findByBusinessTypeAndIndustry(
            BusinessInformation.BusinessType businessType,
            BusinessInformation.Industry industry
    );

    /**
     * Find business information by state and business type
     */
    List<BusinessInformation> findByStateAndBusinessType(
            String state,
            BusinessInformation.BusinessType businessType
    );

    /**
     * Count businesses by state
     */
    @Query("SELECT COUNT(b) FROM BusinessInformation b WHERE b.state = :state")
    long countByState(@Param("state") String state);

    /**
     * Count businesses by business type
     */
    long countByBusinessType(BusinessInformation.BusinessType businessType);

    /**
     * Count businesses by industry
     */
    long countByIndustry(BusinessInformation.Industry industry);

    /**
     * Check if business name already exists (for uniqueness validation)
     */
    boolean existsByNameIgnoreCase(String name);

    /**
     * Find all distinct states
     */
    @Query("SELECT DISTINCT b.state FROM BusinessInformation b ORDER BY b.state")
    List<String> findDistinctStates();

    /**
     * Find businesses created in the last N days
     */
    @Query("SELECT b FROM BusinessInformation b WHERE b.createdAt >= CURRENT_TIMESTAMP - :days DAY")
    List<BusinessInformation> findRecentBusinesses(@Param("days") int days);
}