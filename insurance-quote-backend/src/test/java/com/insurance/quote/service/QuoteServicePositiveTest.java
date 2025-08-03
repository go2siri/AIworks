package com.insurance.quote.service;

import com.insurance.quote.dto.BusinessInformationDto;
import com.insurance.quote.dto.CoverageOptionDto;
import com.insurance.quote.dto.QuoteDto;
import com.insurance.quote.entity.BusinessInformation;
import com.insurance.quote.entity.CoverageOption;
import com.insurance.quote.entity.Quote;
import com.insurance.quote.mapper.QuoteMapper;
import com.insurance.quote.repository.BusinessInformationRepository;
import com.insurance.quote.repository.CoverageOptionRepository;
import com.insurance.quote.repository.QuoteRepository;
import com.insurance.quote.service.impl.QuoteServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Positive test scenarios for QuoteService
 */
@ExtendWith(MockitoExtension.class)
class QuoteServicePositiveTest {

    private static final Logger logger = LoggerFactory.getLogger(QuoteServicePositiveTest.class);

    @Mock
    private QuoteRepository quoteRepository;

    @Mock
    private BusinessInformationRepository businessInfoRepository;

    @Mock
    private CoverageOptionRepository coverageOptionRepository;

    @Mock
    private QuoteMapper quoteMapper;

    @InjectMocks
    private QuoteServiceImpl quoteService;

    private QuoteDto testQuoteDto;
    private Quote testQuote;
    private BusinessInformationDto testBusinessInfoDto;
    private BusinessInformation testBusinessInfo;

    @BeforeEach
    void setUp() {
        logger.info("Setting up test data");
        
        // Initialize test business information DTO
        testBusinessInfoDto = new BusinessInformationDto();
        testBusinessInfoDto.setName("Test Business LLC");
        testBusinessInfoDto.setBusinessType(BusinessInformation.BusinessType.RETAIL);
        testBusinessInfoDto.setIndustry(BusinessInformation.Industry.RETAIL_TRADE);
        testBusinessInfoDto.setState("CA");

        // Initialize test business information entity
        testBusinessInfo = new BusinessInformation();
        testBusinessInfo.setId(1L);
        testBusinessInfo.setName("Test Business LLC");
        testBusinessInfo.setBusinessType(BusinessInformation.BusinessType.RETAIL);
        testBusinessInfo.setIndustry(BusinessInformation.Industry.RETAIL_TRADE);
        testBusinessInfo.setState("CA");

        // Initialize test quote DTO
        testQuoteDto = new QuoteDto();
        testQuoteDto.setBusinessInformation(testBusinessInfoDto);
        testQuoteDto.setStatus(Quote.QuoteStatus.DRAFT);
        testQuoteDto.setTotalPremium(BigDecimal.ZERO);

        // Initialize test quote entity
        testQuote = new Quote();
        testQuote.setId(1L);
        testQuote.setBusinessInformation(testBusinessInfo);
        testQuote.setStatus(Quote.QuoteStatus.DRAFT);
        testQuote.setTotalPremium(BigDecimal.ZERO);
        testQuote.setQuoteNumber("IQ-20240101-0001");
    }

    @Test
    @DisplayName("Should successfully create a new quote")
    void testCreateQuote_Success() {
        logger.info("Testing successful quote creation");
        
        // Given
        when(quoteMapper.toEntity(any(QuoteDto.class))).thenReturn(testQuote);
        when(quoteRepository.save(any(Quote.class))).thenReturn(testQuote);
        when(quoteMapper.toDto(any(Quote.class))).thenReturn(testQuoteDto);
        when(quoteRepository.existsByQuoteNumber(anyString())).thenReturn(false);

        // When
        QuoteDto result = quoteService.createQuote(testQuoteDto);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getBusinessInformation().getName()).isEqualTo("Test Business LLC");
        verify(quoteRepository, times(1)).save(any(Quote.class));
        verify(quoteMapper, times(1)).toEntity(any(QuoteDto.class));
        verify(quoteMapper, times(1)).toDto(any(Quote.class));
    }

    @Test
    @DisplayName("Should successfully update an existing quote")
    void testUpdateQuote_Success() {
        logger.info("Testing successful quote update");
        
        // Given
        testQuote.setStatus(Quote.QuoteStatus.DRAFT);
        when(quoteRepository.findById(1L)).thenReturn(Optional.of(testQuote));
        when(quoteRepository.save(any(Quote.class))).thenReturn(testQuote);
        when(quoteMapper.toDto(any(Quote.class))).thenReturn(testQuoteDto);

        // When
        QuoteDto result = quoteService.updateQuote(1L, testQuoteDto);

        // Then
        assertThat(result).isNotNull();
        verify(quoteRepository, times(1)).findById(1L);
        verify(quoteRepository, times(1)).save(any(Quote.class));
    }

    @Test
    @DisplayName("Should successfully retrieve quote by ID")
    void testGetQuoteById_Success() {
        logger.info("Testing successful quote retrieval by ID");
        
        // Given
        when(quoteRepository.findById(1L)).thenReturn(Optional.of(testQuote));
        when(quoteMapper.toDto(any(Quote.class))).thenReturn(testQuoteDto);

        // When
        Optional<QuoteDto> result = quoteService.getQuoteById(1L);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getBusinessInformation().getName()).isEqualTo("Test Business LLC");
        verify(quoteRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should successfully retrieve quote by quote number")
    void testGetQuoteByNumber_Success() {
        logger.info("Testing successful quote retrieval by quote number");
        
        // Given
        String quoteNumber = "IQ-20240101-0001";
        when(quoteRepository.findByQuoteNumber(quoteNumber)).thenReturn(Optional.of(testQuote));
        when(quoteMapper.toDto(any(Quote.class))).thenReturn(testQuoteDto);

        // When
        Optional<QuoteDto> result = quoteService.getQuoteByNumber(quoteNumber);

        // Then
        assertThat(result).isPresent();
        verify(quoteRepository, times(1)).findByQuoteNumber(quoteNumber);
    }

    @Test
    @DisplayName("Should successfully retrieve all quotes with pagination")
    void testGetAllQuotes_Success() {
        logger.info("Testing successful retrieval of all quotes with pagination");
        
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Page<Quote> quotePage = new PageImpl<>(Arrays.asList(testQuote));
        when(quoteRepository.findAll(pageable)).thenReturn(quotePage);
        when(quoteMapper.toDto(any(Quote.class))).thenReturn(testQuoteDto);

        // When
        Page<QuoteDto> result = quoteService.getAllQuotes(pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getBusinessInformation().getName()).isEqualTo("Test Business LLC");
        verify(quoteRepository, times(1)).findAll(pageable);
    }

    @Test
    @DisplayName("Should successfully retrieve quotes by status")
    void testGetQuotesByStatus_Success() {
        logger.info("Testing successful retrieval of quotes by status");
        
        // Given
        Quote.QuoteStatus status = Quote.QuoteStatus.DRAFT;
        when(quoteRepository.findByStatus(status)).thenReturn(Arrays.asList(testQuote));
        when(quoteMapper.toDto(any(Quote.class))).thenReturn(testQuoteDto);

        // When
        List<QuoteDto> result = quoteService.getQuotesByStatus(status);

        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStatus()).isEqualTo(Quote.QuoteStatus.DRAFT);
        verify(quoteRepository, times(1)).findByStatus(status);
    }

    @Test
    @DisplayName("Should successfully calculate quote premium")
    void testCalculateQuotePremium_Success() {
        logger.info("Testing successful quote premium calculation");
        
        // Given
        CoverageOption option1 = new CoverageOption("General Liability", 
                CoverageOption.CoverageType.GENERAL_LIABILITY, 
                new BigDecimal("500.00"), "Description");
        option1.setIsSelected(true);
        
        CoverageOption option2 = new CoverageOption("Property", 
                CoverageOption.CoverageType.PROPERTY, 
                new BigDecimal("750.00"), "Description");
        option2.setIsSelected(true);
        
        testQuote.getCoverageOptions().add(option1);
        testQuote.getCoverageOptions().add(option2);
        
        when(quoteRepository.findById(1L)).thenReturn(Optional.of(testQuote));

        // When
        BigDecimal result = quoteService.calculateQuotePremium(1L);

        // Then
        assertThat(result).isEqualTo(new BigDecimal("1250.00"));
        verify(quoteRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should successfully submit a saved quote")
    void testSubmitQuote_Success() {
        logger.info("Testing successful quote submission");
        
        // Given
        testQuote.setStatus(Quote.QuoteStatus.SAVED);
        CoverageOption selectedOption = new CoverageOption();
        selectedOption.setIsSelected(true);
        testQuote.getCoverageOptions().add(selectedOption);
        
        when(quoteRepository.findById(1L)).thenReturn(Optional.of(testQuote));
        when(quoteRepository.save(any(Quote.class))).thenReturn(testQuote);
        when(quoteMapper.toDto(any(Quote.class))).thenReturn(testQuoteDto);

        // When
        QuoteDto result = quoteService.submitQuote(1L);

        // Then
        assertThat(result).isNotNull();
        verify(quoteRepository, times(1)).findById(1L);
        verify(quoteRepository, times(1)).save(any(Quote.class));
        assertThat(testQuote.getStatus()).isEqualTo(Quote.QuoteStatus.SUBMITTED);
    }

    @Test
    @DisplayName("Should successfully approve a submitted quote")
    void testApproveQuote_Success() {
        logger.info("Testing successful quote approval");
        
        // Given
        testQuote.setStatus(Quote.QuoteStatus.SUBMITTED);
        when(quoteRepository.findById(1L)).thenReturn(Optional.of(testQuote));
        when(quoteRepository.save(any(Quote.class))).thenReturn(testQuote);
        when(quoteMapper.toDto(any(Quote.class))).thenReturn(testQuoteDto);

        // When
        QuoteDto result = quoteService.approveQuote(1L);

        // Then
        assertThat(result).isNotNull();
        verify(quoteRepository, times(1)).findById(1L);
        verify(quoteRepository, times(1)).save(any(Quote.class));
        assertThat(testQuote.getStatus()).isEqualTo(Quote.QuoteStatus.APPROVED);
    }

    @Test
    @DisplayName("Should successfully generate unique quote number")
    void testGenerateQuoteNumber_Success() {
        logger.info("Testing successful quote number generation");
        
        // Given
        when(quoteRepository.existsByQuoteNumber(anyString())).thenReturn(false);

        // When
        String quoteNumber = quoteService.generateQuoteNumber();

        // Then
        assertThat(quoteNumber).isNotNull();
        assertThat(quoteNumber).startsWith("IQ-");
        assertThat(quoteNumber).matches("IQ-\\d{14}-\\d{4}");
    }

    @Test
    @DisplayName("Should successfully delete a draft quote")
    void testDeleteQuote_Success() {
        logger.info("Testing successful quote deletion");
        
        // Given
        testQuote.setStatus(Quote.QuoteStatus.DRAFT);
        when(quoteRepository.findById(1L)).thenReturn(Optional.of(testQuote));
        doNothing().when(quoteRepository).delete(any(Quote.class));

        // When
        quoteService.deleteQuote(1L);

        // Then
        verify(quoteRepository, times(1)).findById(1L);
        verify(quoteRepository, times(1)).delete(testQuote);
    }

    @Test
    @DisplayName("Should successfully get quote statistics")
    void testGetQuoteStatistics_Success() {
        logger.info("Testing successful retrieval of quote statistics");
        
        // Given
        when(quoteRepository.count()).thenReturn(10L);
        when(quoteRepository.countByStatus(Quote.QuoteStatus.DRAFT)).thenReturn(3L);
        when(quoteRepository.countByStatus(Quote.QuoteStatus.SAVED)).thenReturn(2L);
        when(quoteRepository.countByStatus(Quote.QuoteStatus.SUBMITTED)).thenReturn(2L);
        when(quoteRepository.countByStatus(Quote.QuoteStatus.APPROVED)).thenReturn(2L);
        when(quoteRepository.countByStatus(Quote.QuoteStatus.REJECTED)).thenReturn(1L);
        when(quoteRepository.countByStatus(Quote.QuoteStatus.EXPIRED)).thenReturn(0L);
        
        List<Quote> quotes = Arrays.asList(
                createQuoteWithPremium(new BigDecimal("1000")),
                createQuoteWithPremium(new BigDecimal("2000"))
        );
        when(quoteRepository.findAll()).thenReturn(quotes);

        // When
        QuoteService.QuoteStatistics stats = quoteService.getQuoteStatistics();

        // Then
        assertThat(stats).isNotNull();
        assertThat(stats.getTotalQuotes()).isEqualTo(10L);
        assertThat(stats.getDraftQuotes()).isEqualTo(3L);
        assertThat(stats.getTotalPremiumValue()).isEqualTo(new BigDecimal("3000"));
        assertThat(stats.getAveragePremium()).isEqualTo(new BigDecimal("1500.00"));
    }

    // Helper method
    private Quote createQuoteWithPremium(BigDecimal premium) {
        Quote quote = new Quote();
        quote.setTotalPremium(premium);
        return quote;
    }
}