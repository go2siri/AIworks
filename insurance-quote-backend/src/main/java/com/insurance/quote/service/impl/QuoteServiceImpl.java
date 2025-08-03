package com.insurance.quote.service.impl;

import com.insurance.quote.dto.BusinessInformationDto;
import com.insurance.quote.dto.CoverageOptionDto;
import com.insurance.quote.dto.QuoteDto;
import com.insurance.quote.entity.BusinessInformation;
import com.insurance.quote.entity.CoverageOption;
import com.insurance.quote.entity.Quote;
import com.insurance.quote.exception.ResourceNotFoundException;
import com.insurance.quote.exception.InvalidQuoteStateException;
import com.insurance.quote.mapper.QuoteMapper;
import com.insurance.quote.repository.BusinessInformationRepository;
import com.insurance.quote.repository.CoverageOptionRepository;
import com.insurance.quote.repository.QuoteRepository;
import com.insurance.quote.service.QuoteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * Implementation of QuoteService
 */
@Service
@Transactional
public class QuoteServiceImpl implements QuoteService {

    private static final Logger logger = LoggerFactory.getLogger(QuoteServiceImpl.class);

    private final QuoteRepository quoteRepository;
    private final BusinessInformationRepository businessInfoRepository;
    private final CoverageOptionRepository coverageOptionRepository;
    private final QuoteMapper quoteMapper;

    @Value("${app.quote.validity-days:30}")
    private int quoteValidityDays;

    @Value("${app.quote.quote-number-prefix:IQ}")
    private String quoteNumberPrefix;

    public QuoteServiceImpl(QuoteRepository quoteRepository,
                           BusinessInformationRepository businessInfoRepository,
                           CoverageOptionRepository coverageOptionRepository,
                           QuoteMapper quoteMapper) {
        this.quoteRepository = quoteRepository;
        this.businessInfoRepository = businessInfoRepository;
        this.coverageOptionRepository = coverageOptionRepository;
        this.quoteMapper = quoteMapper;
    }

    @Override
    public QuoteDto createQuote(QuoteDto quoteDto) {
        logger.info("Creating new quote for business: {}", quoteDto.getBusinessInformation().getName());
        
        // Map DTO to entity
        Quote quote = quoteMapper.toEntity(quoteDto);
        
        // Generate quote number
        quote.setQuoteNumber(generateQuoteNumber());
        
        // Set validity period
        quote.setValidUntil(LocalDateTime.now().plusDays(quoteValidityDays));
        
        // Initialize default coverage options if none provided
        if (quote.getCoverageOptions().isEmpty()) {
            initializeDefaultCoverageOptions(quote);
        }
        
        // Set quote reference in coverage options
        for (CoverageOption option : quote.getCoverageOptions()) {
            option.setQuote(quote);
        }
        
        // Calculate total premium
        quote.calculateTotalPremium();
        
        // Save quote
        Quote savedQuote = quoteRepository.save(quote);
        logger.info("Created quote with ID: {} and number: {}", savedQuote.getId(), savedQuote.getQuoteNumber());
        
        return quoteMapper.toDto(savedQuote);
    }

    @Override
    public QuoteDto updateQuote(Long id, QuoteDto quoteDto) {
        logger.info("Updating quote with ID: {}", id);
        
        Quote existingQuote = quoteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Quote not found with id: " + id));
        
        // Check if quote can be updated
        if (existingQuote.getStatus() == Quote.QuoteStatus.APPROVED ||
            existingQuote.getStatus() == Quote.QuoteStatus.REJECTED) {
            throw new InvalidQuoteStateException("Cannot update quote in " + existingQuote.getStatus() + " status");
        }
        
        // Update business information
        BusinessInformation businessInfo = existingQuote.getBusinessInformation();
        BusinessInformationDto businessInfoDto = quoteDto.getBusinessInformation();
        businessInfo.setName(businessInfoDto.getName());
        businessInfo.setBusinessType(businessInfoDto.getBusinessType());
        businessInfo.setIndustry(businessInfoDto.getIndustry());
        businessInfo.setState(businessInfoDto.getState());
        
        // Update coverage options
        updateCoverageOptions(existingQuote, quoteDto.getCoverageOptions());
        
        // Update other fields
        existingQuote.setRiskRating(quoteDto.getRiskRating());
        existingQuote.setUnderwriterNotes(quoteDto.getUnderwriterNotes());
        
        // Update status if changed
        if (quoteDto.getStatus() != null && quoteDto.getStatus() != existingQuote.getStatus()) {
            validateStatusTransition(existingQuote.getStatus(), quoteDto.getStatus());
            existingQuote.setStatus(quoteDto.getStatus());
        }
        
        // Recalculate premium
        existingQuote.calculateTotalPremium();
        
        Quote updatedQuote = quoteRepository.save(existingQuote);
        logger.info("Updated quote with ID: {}", updatedQuote.getId());
        
        return quoteMapper.toDto(updatedQuote);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<QuoteDto> getQuoteById(Long id) {
        logger.debug("Fetching quote with ID: {}", id);
        return quoteRepository.findById(id)
                .map(quoteMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<QuoteDto> getQuoteByNumber(String quoteNumber) {
        logger.debug("Fetching quote with number: {}", quoteNumber);
        return quoteRepository.findByQuoteNumber(quoteNumber)
                .map(quoteMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<QuoteDto> getAllQuotes(Pageable pageable) {
        logger.debug("Fetching all quotes with pagination: {}", pageable);
        return quoteRepository.findAll(pageable)
                .map(quoteMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<QuoteDto> getQuotesByStatus(Quote.QuoteStatus status) {
        logger.debug("Fetching quotes with status: {}", status);
        return quoteRepository.findByStatus(status).stream()
                .map(quoteMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<QuoteDto> searchQuotesByBusinessName(String businessName, Pageable pageable) {
        logger.debug("Searching quotes by business name: {}", businessName);
        return quoteRepository.findByBusinessNameContainingIgnoreCase(businessName, pageable)
                .map(quoteMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<QuoteDto> getQuotesByState(String state) {
        logger.debug("Fetching quotes for state: {}", state);
        return quoteRepository.findByState(state).stream()
                .map(quoteMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<QuoteDto> getQuotesCreatedBetween(LocalDateTime startDate, LocalDateTime endDate) {
        logger.debug("Fetching quotes created between {} and {}", startDate, endDate);
        return quoteRepository.findByCreatedAtBetween(startDate, endDate).stream()
                .map(quoteMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<QuoteDto> getExpiredQuotes() {
        logger.debug("Fetching expired quotes");
        return quoteRepository.findExpiredQuotes(LocalDateTime.now()).stream()
                .map(quoteMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteQuote(Long id) {
        logger.info("Deleting quote with ID: {}", id);
        
        Quote quote = quoteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Quote not found with id: " + id));
        
        // Only allow deletion of draft quotes
        if (quote.getStatus() != Quote.QuoteStatus.DRAFT) {
            throw new InvalidQuoteStateException("Only draft quotes can be deleted");
        }
        
        quoteRepository.delete(quote);
        logger.info("Deleted quote with ID: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal calculateQuotePremium(Long quoteId) {
        logger.debug("Calculating premium for quote ID: {}", quoteId);
        
        Quote quote = quoteRepository.findById(quoteId)
                .orElseThrow(() -> new ResourceNotFoundException("Quote not found with id: " + quoteId));
        
        quote.calculateTotalPremium();
        return quote.getTotalPremium();
    }

    @Override
    public QuoteDto submitQuote(Long id) {
        logger.info("Submitting quote with ID: {}", id);
        
        Quote quote = quoteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Quote not found with id: " + id));
        
        // Validate quote can be submitted
        if (quote.getStatus() != Quote.QuoteStatus.SAVED) {
            throw new InvalidQuoteStateException("Only saved quotes can be submitted");
        }
        
        // Ensure quote has at least one selected coverage option
        boolean hasSelectedCoverage = quote.getCoverageOptions().stream()
                .anyMatch(option -> Boolean.TRUE.equals(option.getIsSelected()));
        
        if (!hasSelectedCoverage) {
            throw new InvalidQuoteStateException("Quote must have at least one selected coverage option");
        }
        
        quote.setStatus(Quote.QuoteStatus.SUBMITTED);
        Quote updatedQuote = quoteRepository.save(quote);
        
        logger.info("Submitted quote with ID: {}", id);
        return quoteMapper.toDto(updatedQuote);
    }

    @Override
    public QuoteDto approveQuote(Long id) {
        logger.info("Approving quote with ID: {}", id);
        
        Quote quote = quoteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Quote not found with id: " + id));
        
        if (quote.getStatus() != Quote.QuoteStatus.SUBMITTED) {
            throw new InvalidQuoteStateException("Only submitted quotes can be approved");
        }
        
        quote.setStatus(Quote.QuoteStatus.APPROVED);
        Quote updatedQuote = quoteRepository.save(quote);
        
        logger.info("Approved quote with ID: {}", id);
        return quoteMapper.toDto(updatedQuote);
    }

    @Override
    public QuoteDto rejectQuote(Long id, String reason) {
        logger.info("Rejecting quote with ID: {} for reason: {}", id, reason);
        
        Quote quote = quoteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Quote not found with id: " + id));
        
        if (quote.getStatus() != Quote.QuoteStatus.SUBMITTED) {
            throw new InvalidQuoteStateException("Only submitted quotes can be rejected");
        }
        
        quote.setStatus(Quote.QuoteStatus.REJECTED);
        quote.setUnderwriterNotes(quote.getUnderwriterNotes() + "\nRejection reason: " + reason);
        Quote updatedQuote = quoteRepository.save(quote);
        
        logger.info("Rejected quote with ID: {}", id);
        return quoteMapper.toDto(updatedQuote);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isQuoteNumberUnique(String quoteNumber) {
        return !quoteRepository.existsByQuoteNumber(quoteNumber);
    }

    @Override
    public String generateQuoteNumber() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String random = String.format("%04d", new Random().nextInt(10000));
        String quoteNumber = quoteNumberPrefix + "-" + timestamp + "-" + random;
        
        // Ensure uniqueness
        while (!isQuoteNumberUnique(quoteNumber)) {
            random = String.format("%04d", new Random().nextInt(10000));
            quoteNumber = quoteNumberPrefix + "-" + timestamp + "-" + random;
        }
        
        return quoteNumber;
    }

    @Override
    @Transactional(readOnly = true)
    public QuoteStatistics getQuoteStatistics() {
        logger.debug("Calculating quote statistics");
        
        QuoteStatistics stats = new QuoteStatistics();
        stats.setTotalQuotes(quoteRepository.count());
        stats.setDraftQuotes(quoteRepository.countByStatus(Quote.QuoteStatus.DRAFT));
        stats.setSavedQuotes(quoteRepository.countByStatus(Quote.QuoteStatus.SAVED));
        stats.setSubmittedQuotes(quoteRepository.countByStatus(Quote.QuoteStatus.SUBMITTED));
        stats.setApprovedQuotes(quoteRepository.countByStatus(Quote.QuoteStatus.APPROVED));
        stats.setRejectedQuotes(quoteRepository.countByStatus(Quote.QuoteStatus.REJECTED));
        stats.setExpiredQuotes(quoteRepository.countByStatus(Quote.QuoteStatus.EXPIRED));
        
        // Calculate premium statistics
        List<Quote> allQuotes = quoteRepository.findAll();
        BigDecimal totalPremium = allQuotes.stream()
                .map(Quote::getTotalPremium)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        stats.setTotalPremiumValue(totalPremium);
        
        if (!allQuotes.isEmpty()) {
            stats.setAveragePremium(totalPremium.divide(
                    BigDecimal.valueOf(allQuotes.size()), 2, RoundingMode.HALF_UP));
        } else {
            stats.setAveragePremium(BigDecimal.ZERO);
        }
        
        return stats;
    }

    // Helper methods
    private void initializeDefaultCoverageOptions(Quote quote) {
        // Create default coverage options based on configuration
        CoverageOption generalLiability = new CoverageOption(
                "General Liability",
                CoverageOption.CoverageType.GENERAL_LIABILITY,
                new BigDecimal("500.00"),
                "General liability insurance protects your business from claims"
        );
        
        CoverageOption property = new CoverageOption(
                "Property",
                CoverageOption.CoverageType.PROPERTY,
                new BigDecimal("750.00"),
                "Property insurance covers your business property"
        );
        
        CoverageOption additional = new CoverageOption(
                "Additional Coverage Options",
                CoverageOption.CoverageType.ADDITIONAL,
                new BigDecimal("300.00"),
                "Additional coverage options include cyber liability"
        );
        
        quote.addCoverageOption(generalLiability);
        quote.addCoverageOption(property);
        quote.addCoverageOption(additional);
    }

    private void updateCoverageOptions(Quote quote, List<CoverageOptionDto> optionDtos) {
        // Update existing coverage options
        for (CoverageOptionDto dto : optionDtos) {
            Optional<CoverageOption> existingOption = quote.getCoverageOptions().stream()
                    .filter(opt -> opt.getCoverageType() == dto.getCoverageType())
                    .findFirst();
            
            if (existingOption.isPresent()) {
                CoverageOption option = existingOption.get();
                option.setName(dto.getName());
                option.setPremium(dto.getPremium());
                option.setDescription(dto.getDescription());
                option.setIsSelected(dto.getIsSelected());
                option.setIsActive(dto.getIsActive());
            }
        }
    }

    private void validateStatusTransition(Quote.QuoteStatus currentStatus, Quote.QuoteStatus newStatus) {
        // Define valid status transitions
        boolean validTransition = switch (currentStatus) {
            case DRAFT -> newStatus == Quote.QuoteStatus.SAVED;
            case SAVED -> newStatus == Quote.QuoteStatus.SUBMITTED || newStatus == Quote.QuoteStatus.DRAFT;
            case SUBMITTED -> newStatus == Quote.QuoteStatus.APPROVED || newStatus == Quote.QuoteStatus.REJECTED;
            case APPROVED, REJECTED, EXPIRED -> false;
        };
        
        if (!validTransition) {
            throw new InvalidQuoteStateException(
                    "Invalid status transition from " + currentStatus + " to " + newStatus);
        }
    }
}