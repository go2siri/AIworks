package com.insurance.quote.service;

import com.insurance.quote.dto.BusinessInformationDto;
import com.insurance.quote.dto.QuoteDto;
import com.insurance.quote.entity.BusinessInformation;
import com.insurance.quote.entity.Quote;
import com.insurance.quote.exception.InvalidQuoteStateException;
import com.insurance.quote.exception.ResourceNotFoundException;
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

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Negative test scenarios for QuoteService
 */
@ExtendWith(MockitoExtension.class)
class QuoteServiceNegativeTest {

    private static final Logger logger = LoggerFactory.getLogger(QuoteServiceNegativeTest.class);

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
        logger.info("Setting up negative test data");
        
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

        // Initialize test quote entity
        testQuote = new Quote();
        testQuote.setId(1L);
        testQuote.setBusinessInformation(testBusinessInfo);
        testQuote.setStatus(Quote.QuoteStatus.DRAFT);
        testQuote.setQuoteNumber("IQ-20240101-0001");
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when updating non-existent quote")
    void testUpdateQuote_QuoteNotFound() {
        logger.info("Testing update of non-existent quote");
        
        // Given
        Long nonExistentId = 999L;
        when(quoteRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> quoteService.updateQuote(nonExistentId, testQuoteDto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Quote not found with id: " + nonExistentId);
        
        verify(quoteRepository, times(1)).findById(nonExistentId);
        verify(quoteRepository, never()).save(any(Quote.class));
    }

    @Test
    @DisplayName("Should throw InvalidQuoteStateException when updating approved quote")
    void testUpdateQuote_ApprovedQuoteCannotBeUpdated() {
        logger.info("Testing update of approved quote");
        
        // Given
        testQuote.setStatus(Quote.QuoteStatus.APPROVED);
        when(quoteRepository.findById(1L)).thenReturn(Optional.of(testQuote));

        // When & Then
        assertThatThrownBy(() -> quoteService.updateQuote(1L, testQuoteDto))
                .isInstanceOf(InvalidQuoteStateException.class)
                .hasMessageContaining("Cannot update quote in APPROVED status");
        
        verify(quoteRepository, times(1)).findById(1L);
        verify(quoteRepository, never()).save(any(Quote.class));
    }

    @Test
    @DisplayName("Should throw InvalidQuoteStateException when updating rejected quote")
    void testUpdateQuote_RejectedQuoteCannotBeUpdated() {
        logger.info("Testing update of rejected quote");
        
        // Given
        testQuote.setStatus(Quote.QuoteStatus.REJECTED);
        when(quoteRepository.findById(1L)).thenReturn(Optional.of(testQuote));

        // When & Then
        assertThatThrownBy(() -> quoteService.updateQuote(1L, testQuoteDto))
                .isInstanceOf(InvalidQuoteStateException.class)
                .hasMessageContaining("Cannot update quote in REJECTED status");
        
        verify(quoteRepository, times(1)).findById(1L);
        verify(quoteRepository, never()).save(any(Quote.class));
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when deleting non-existent quote")
    void testDeleteQuote_QuoteNotFound() {
        logger.info("Testing deletion of non-existent quote");
        
        // Given
        Long nonExistentId = 999L;
        when(quoteRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> quoteService.deleteQuote(nonExistentId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Quote not found with id: " + nonExistentId);
        
        verify(quoteRepository, times(1)).findById(nonExistentId);
        verify(quoteRepository, never()).delete(any(Quote.class));
    }

    @Test
    @DisplayName("Should throw InvalidQuoteStateException when deleting non-draft quote")
    void testDeleteQuote_OnlyDraftQuotesCanBeDeleted() {
        logger.info("Testing deletion of non-draft quote");
        
        // Given
        testQuote.setStatus(Quote.QuoteStatus.SAVED);
        when(quoteRepository.findById(1L)).thenReturn(Optional.of(testQuote));

        // When & Then
        assertThatThrownBy(() -> quoteService.deleteQuote(1L))
                .isInstanceOf(InvalidQuoteStateException.class)
                .hasMessageContaining("Only draft quotes can be deleted");
        
        verify(quoteRepository, times(1)).findById(1L);
        verify(quoteRepository, never()).delete(any(Quote.class));
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when calculating premium for non-existent quote")
    void testCalculateQuotePremium_QuoteNotFound() {
        logger.info("Testing premium calculation for non-existent quote");
        
        // Given
        Long nonExistentId = 999L;
        when(quoteRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> quoteService.calculateQuotePremium(nonExistentId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Quote not found with id: " + nonExistentId);
        
        verify(quoteRepository, times(1)).findById(nonExistentId);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when submitting non-existent quote")
    void testSubmitQuote_QuoteNotFound() {
        logger.info("Testing submission of non-existent quote");
        
        // Given
        Long nonExistentId = 999L;
        when(quoteRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> quoteService.submitQuote(nonExistentId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Quote not found with id: " + nonExistentId);
        
        verify(quoteRepository, times(1)).findById(nonExistentId);
        verify(quoteRepository, never()).save(any(Quote.class));
    }

    @Test
    @DisplayName("Should throw InvalidQuoteStateException when submitting non-saved quote")
    void testSubmitQuote_OnlySavedQuotesCanBeSubmitted() {
        logger.info("Testing submission of non-saved quote");
        
        // Given
        testQuote.setStatus(Quote.QuoteStatus.DRAFT);
        when(quoteRepository.findById(1L)).thenReturn(Optional.of(testQuote));

        // When & Then
        assertThatThrownBy(() -> quoteService.submitQuote(1L))
                .isInstanceOf(InvalidQuoteStateException.class)
                .hasMessageContaining("Only saved quotes can be submitted");
        
        verify(quoteRepository, times(1)).findById(1L);
        verify(quoteRepository, never()).save(any(Quote.class));
    }

    @Test
    @DisplayName("Should throw InvalidQuoteStateException when submitting quote without coverage")
    void testSubmitQuote_RequiresSelectedCoverage() {
        logger.info("Testing submission of quote without selected coverage");
        
        // Given
        testQuote.setStatus(Quote.QuoteStatus.SAVED);
        // No coverage options added, so no selected coverage
        when(quoteRepository.findById(1L)).thenReturn(Optional.of(testQuote));

        // When & Then
        assertThatThrownBy(() -> quoteService.submitQuote(1L))
                .isInstanceOf(InvalidQuoteStateException.class)
                .hasMessageContaining("Quote must have at least one selected coverage option");
        
        verify(quoteRepository, times(1)).findById(1L);
        verify(quoteRepository, never()).save(any(Quote.class));
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when approving non-existent quote")
    void testApproveQuote_QuoteNotFound() {
        logger.info("Testing approval of non-existent quote");
        
        // Given
        Long nonExistentId = 999L;
        when(quoteRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> quoteService.approveQuote(nonExistentId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Quote not found with id: " + nonExistentId);
        
        verify(quoteRepository, times(1)).findById(nonExistentId);
        verify(quoteRepository, never()).save(any(Quote.class));
    }

    @Test
    @DisplayName("Should throw InvalidQuoteStateException when approving non-submitted quote")
    void testApproveQuote_OnlySubmittedQuotesCanBeApproved() {
        logger.info("Testing approval of non-submitted quote");
        
        // Given
        testQuote.setStatus(Quote.QuoteStatus.SAVED);
        when(quoteRepository.findById(1L)).thenReturn(Optional.of(testQuote));

        // When & Then
        assertThatThrownBy(() -> quoteService.approveQuote(1L))
                .isInstanceOf(InvalidQuoteStateException.class)
                .hasMessageContaining("Only submitted quotes can be approved");
        
        verify(quoteRepository, times(1)).findById(1L);
        verify(quoteRepository, never()).save(any(Quote.class));
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when rejecting non-existent quote")
    void testRejectQuote_QuoteNotFound() {
        logger.info("Testing rejection of non-existent quote");
        
        // Given
        Long nonExistentId = 999L;
        String reason = "Test rejection reason";
        when(quoteRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> quoteService.rejectQuote(nonExistentId, reason))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Quote not found with id: " + nonExistentId);
        
        verify(quoteRepository, times(1)).findById(nonExistentId);
        verify(quoteRepository, never()).save(any(Quote.class));
    }

    @Test
    @DisplayName("Should throw InvalidQuoteStateException when rejecting non-submitted quote")
    void testRejectQuote_OnlySubmittedQuotesCanBeRejected() {
        logger.info("Testing rejection of non-submitted quote");
        
        // Given
        testQuote.setStatus(Quote.QuoteStatus.DRAFT);
        String reason = "Test rejection reason";
        when(quoteRepository.findById(1L)).thenReturn(Optional.of(testQuote));

        // When & Then
        assertThatThrownBy(() -> quoteService.rejectQuote(1L, reason))
                .isInstanceOf(InvalidQuoteStateException.class)
                .hasMessageContaining("Only submitted quotes can be rejected");
        
        verify(quoteRepository, times(1)).findById(1L);
        verify(quoteRepository, never()).save(any(Quote.class));
    }

    @Test
    @DisplayName("Should throw InvalidQuoteStateException for invalid status transitions")
    void testUpdateQuote_InvalidStatusTransition() {
        logger.info("Testing invalid status transition");
        
        // Given
        testQuote.setStatus(Quote.QuoteStatus.DRAFT);
        testQuoteDto.setStatus(Quote.QuoteStatus.APPROVED); // Invalid: DRAFT -> APPROVED
        when(quoteRepository.findById(1L)).thenReturn(Optional.of(testQuote));

        // When & Then
        assertThatThrownBy(() -> quoteService.updateQuote(1L, testQuoteDto))
                .isInstanceOf(InvalidQuoteStateException.class)
                .hasMessageContaining("Invalid status transition from DRAFT to APPROVED");
        
        verify(quoteRepository, times(1)).findById(1L);
        verify(quoteRepository, never()).save(any(Quote.class));
    }

    @Test
    @DisplayName("Should throw InvalidQuoteStateException when transitioning from APPROVED to any other status")
    void testUpdateQuote_ApprovedQuoteCannotChangeStatus() {
        logger.info("Testing status transition from approved state");
        
        // Given
        testQuote.setStatus(Quote.QuoteStatus.APPROVED);
        testQuoteDto.setStatus(Quote.QuoteStatus.DRAFT);
        when(quoteRepository.findById(1L)).thenReturn(Optional.of(testQuote));

        // When & Then
        assertThatThrownBy(() -> quoteService.updateQuote(1L, testQuoteDto))
                .isInstanceOf(InvalidQuoteStateException.class)
                .hasMessageContaining("Cannot update quote in APPROVED status");
        
        verify(quoteRepository, times(1)).findById(1L);
        verify(quoteRepository, never()).save(any(Quote.class));
    }

    @Test
    @DisplayName("Should throw InvalidQuoteStateException when transitioning from REJECTED to any other status")
    void testUpdateQuote_RejectedQuoteCannotChangeStatus() {
        logger.info("Testing status transition from rejected state");
        
        // Given
        testQuote.setStatus(Quote.QuoteStatus.REJECTED);
        testQuoteDto.setStatus(Quote.QuoteStatus.DRAFT);
        when(quoteRepository.findById(1L)).thenReturn(Optional.of(testQuote));

        // When & Then
        assertThatThrownBy(() -> quoteService.updateQuote(1L, testQuoteDto))
                .isInstanceOf(InvalidQuoteStateException.class)
                .hasMessageContaining("Cannot update quote in REJECTED status");
        
        verify(quoteRepository, times(1)).findById(1L);
        verify(quoteRepository, never()).save(any(Quote.class));
    }

    @Test
    @DisplayName("Should throw InvalidQuoteStateException when transitioning SUBMITTED to SAVED")
    void testUpdateQuote_SubmittedCannotGoBackToSaved() {
        logger.info("Testing invalid transition from submitted to saved");
        
        // Given
        testQuote.setStatus(Quote.QuoteStatus.SUBMITTED);
        testQuoteDto.setStatus(Quote.QuoteStatus.SAVED);
        when(quoteRepository.findById(1L)).thenReturn(Optional.of(testQuote));

        // When & Then
        assertThatThrownBy(() -> quoteService.updateQuote(1L, testQuoteDto))
                .isInstanceOf(InvalidQuoteStateException.class)
                .hasMessageContaining("Invalid status transition from SUBMITTED to SAVED");
        
        verify(quoteRepository, times(1)).findById(1L);
        verify(quoteRepository, never()).save(any(Quote.class));
    }
}