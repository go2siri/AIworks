package com.insurance.quote.controller;

import com.insurance.quote.dto.QuoteDto;
import com.insurance.quote.entity.Quote;
import com.insurance.quote.service.QuoteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * REST controller for managing insurance quotes
 */
@RestController
@RequestMapping("/quotes")
@Tag(name = "Quote Management", description = "APIs for managing insurance quotes")
@CrossOrigin
public class QuoteController {

    private static final Logger logger = LoggerFactory.getLogger(QuoteController.class);
    private final QuoteService quoteService;

    public QuoteController(QuoteService quoteService) {
        this.quoteService = quoteService;
    }

    @PostMapping
    @Operation(summary = "Create a new quote", description = "Creates a new insurance quote with business information and coverage options")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Quote created successfully",
                    content = @Content(schema = @Schema(implementation = QuoteDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<QuoteDto> createQuote(@Valid @RequestBody QuoteDto quoteDto) {
        logger.info("REST request to create quote for business: {}", quoteDto.getBusinessInformation().getName());
        QuoteDto createdQuote = quoteService.createQuote(quoteDto);
        return new ResponseEntity<>(createdQuote, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing quote", description = "Updates an existing quote with new information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Quote updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Quote not found"),
            @ApiResponse(responseCode = "422", description = "Quote cannot be updated in current state")
    })
    public ResponseEntity<QuoteDto> updateQuote(
            @PathVariable Long id,
            @Valid @RequestBody QuoteDto quoteDto) {
        logger.info("REST request to update quote: {}", id);
        QuoteDto updatedQuote = quoteService.updateQuote(id, quoteDto);
        return ResponseEntity.ok(updatedQuote);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get quote by ID", description = "Retrieves a quote by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Quote found"),
            @ApiResponse(responseCode = "404", description = "Quote not found")
    })
    public ResponseEntity<QuoteDto> getQuoteById(@PathVariable Long id) {
        logger.info("REST request to get quote: {}", id);
        return quoteService.getQuoteById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/number/{quoteNumber}")
    @Operation(summary = "Get quote by quote number", description = "Retrieves a quote by its unique quote number")
    public ResponseEntity<QuoteDto> getQuoteByNumber(@PathVariable String quoteNumber) {
        logger.info("REST request to get quote by number: {}", quoteNumber);
        return quoteService.getQuoteByNumber(quoteNumber)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    @Operation(summary = "Get all quotes", description = "Retrieves all quotes with pagination support")
    public ResponseEntity<Page<QuoteDto>> getAllQuotes(
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        logger.info("REST request to get all quotes with pagination: {}", pageable);
        Page<QuoteDto> quotes = quoteService.getAllQuotes(pageable);
        return ResponseEntity.ok(quotes);
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get quotes by status", description = "Retrieves all quotes with a specific status")
    public ResponseEntity<List<QuoteDto>> getQuotesByStatus(@PathVariable Quote.QuoteStatus status) {
        logger.info("REST request to get quotes by status: {}", status);
        List<QuoteDto> quotes = quoteService.getQuotesByStatus(status);
        return ResponseEntity.ok(quotes);
    }

    @GetMapping("/search")
    @Operation(summary = "Search quotes by business name", description = "Searches quotes by business name with pagination")
    public ResponseEntity<Page<QuoteDto>> searchQuotesByBusinessName(
            @RequestParam String businessName,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        logger.info("REST request to search quotes by business name: {}", businessName);
        Page<QuoteDto> quotes = quoteService.searchQuotesByBusinessName(businessName, pageable);
        return ResponseEntity.ok(quotes);
    }

    @GetMapping("/state/{state}")
    @Operation(summary = "Get quotes by state", description = "Retrieves all quotes for a specific state")
    public ResponseEntity<List<QuoteDto>> getQuotesByState(@PathVariable String state) {
        logger.info("REST request to get quotes by state: {}", state);
        List<QuoteDto> quotes = quoteService.getQuotesByState(state);
        return ResponseEntity.ok(quotes);
    }

    @GetMapping("/date-range")
    @Operation(summary = "Get quotes by date range", description = "Retrieves quotes created between specified dates")
    public ResponseEntity<List<QuoteDto>> getQuotesByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        logger.info("REST request to get quotes between {} and {}", startDate, endDate);
        List<QuoteDto> quotes = quoteService.getQuotesCreatedBetween(startDate, endDate);
        return ResponseEntity.ok(quotes);
    }

    @GetMapping("/expired")
    @Operation(summary = "Get expired quotes", description = "Retrieves all expired quotes")
    public ResponseEntity<List<QuoteDto>> getExpiredQuotes() {
        logger.info("REST request to get expired quotes");
        List<QuoteDto> quotes = quoteService.getExpiredQuotes();
        return ResponseEntity.ok(quotes);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a quote", description = "Deletes a quote by its ID (only draft quotes can be deleted)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Quote deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Quote not found"),
            @ApiResponse(responseCode = "422", description = "Quote cannot be deleted in current state")
    })
    public ResponseEntity<Void> deleteQuote(@PathVariable Long id) {
        logger.info("REST request to delete quote: {}", id);
        quoteService.deleteQuote(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/premium")
    @Operation(summary = "Calculate quote premium", description = "Calculates the total premium for a quote")
    public ResponseEntity<Map<String, BigDecimal>> calculateQuotePremium(@PathVariable Long id) {
        logger.info("REST request to calculate premium for quote: {}", id);
        BigDecimal premium = quoteService.calculateQuotePremium(id);
        return ResponseEntity.ok(Map.of("totalPremium", premium));
    }

    @PostMapping("/{id}/submit")
    @Operation(summary = "Submit quote for approval", description = "Submits a saved quote for approval")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Quote submitted successfully"),
            @ApiResponse(responseCode = "404", description = "Quote not found"),
            @ApiResponse(responseCode = "422", description = "Quote cannot be submitted in current state")
    })
    public ResponseEntity<QuoteDto> submitQuote(@PathVariable Long id) {
        logger.info("REST request to submit quote: {}", id);
        QuoteDto submittedQuote = quoteService.submitQuote(id);
        return ResponseEntity.ok(submittedQuote);
    }

    @PostMapping("/{id}/approve")
    @Operation(summary = "Approve a quote", description = "Approves a submitted quote")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Quote approved successfully"),
            @ApiResponse(responseCode = "404", description = "Quote not found"),
            @ApiResponse(responseCode = "422", description = "Quote cannot be approved in current state")
    })
    public ResponseEntity<QuoteDto> approveQuote(@PathVariable Long id) {
        logger.info("REST request to approve quote: {}", id);
        QuoteDto approvedQuote = quoteService.approveQuote(id);
        return ResponseEntity.ok(approvedQuote);
    }

    @PostMapping("/{id}/reject")
    @Operation(summary = "Reject a quote", description = "Rejects a submitted quote with a reason")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Quote rejected successfully"),
            @ApiResponse(responseCode = "404", description = "Quote not found"),
            @ApiResponse(responseCode = "422", description = "Quote cannot be rejected in current state")
    })
    public ResponseEntity<QuoteDto> rejectQuote(
            @PathVariable Long id,
            @RequestParam String reason) {
        logger.info("REST request to reject quote: {} with reason: {}", id, reason);
        QuoteDto rejectedQuote = quoteService.rejectQuote(id, reason);
        return ResponseEntity.ok(rejectedQuote);
    }

    @GetMapping("/statistics")
    @Operation(summary = "Get quote statistics", description = "Retrieves statistical information about quotes")
    public ResponseEntity<QuoteService.QuoteStatistics> getQuoteStatistics() {
        logger.info("REST request to get quote statistics");
        QuoteService.QuoteStatistics statistics = quoteService.getQuoteStatistics();
        return ResponseEntity.ok(statistics);
    }

    @GetMapping("/check-number/{quoteNumber}")
    @Operation(summary = "Check if quote number is unique", description = "Checks if a quote number is already in use")
    public ResponseEntity<Map<String, Boolean>> checkQuoteNumber(@PathVariable String quoteNumber) {
        logger.info("REST request to check quote number: {}", quoteNumber);
        boolean isUnique = quoteService.isQuoteNumberUnique(quoteNumber);
        return ResponseEntity.ok(Map.of("isUnique", isUnique));
    }
}