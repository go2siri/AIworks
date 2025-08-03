package com.insurance.quote.mapper;

import com.insurance.quote.dto.BusinessInformationDto;
import com.insurance.quote.dto.CoverageOptionDto;
import com.insurance.quote.dto.QuoteDto;
import com.insurance.quote.entity.BusinessInformation;
import com.insurance.quote.entity.CoverageOption;
import com.insurance.quote.entity.Quote;
import org.mapstruct.*;

import java.util.List;

/**
 * MapStruct mapper for Quote entities and DTOs
 */
@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface QuoteMapper {

    // Quote mappings
    QuoteDto toDto(Quote quote);
    
    Quote toEntity(QuoteDto quoteDto);
    
    List<QuoteDto> toDtoList(List<Quote> quotes);
    
    List<Quote> toEntityList(List<QuoteDto> quoteDtos);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromDto(QuoteDto dto, @MappingTarget Quote entity);

    // BusinessInformation mappings
    BusinessInformationDto toDto(BusinessInformation businessInformation);
    
    BusinessInformation toEntity(BusinessInformationDto businessInformationDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromDto(BusinessInformationDto dto, @MappingTarget BusinessInformation entity);

    // CoverageOption mappings
    CoverageOptionDto toDto(CoverageOption coverageOption);
    
    CoverageOption toEntity(CoverageOptionDto coverageOptionDto);
    
    List<CoverageOptionDto> toCoverageOptionDtoList(List<CoverageOption> coverageOptions);
    
    List<CoverageOption> toCoverageOptionEntityList(List<CoverageOptionDto> coverageOptionDtos);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "quote", ignore = true)
    void updateEntityFromDto(CoverageOptionDto dto, @MappingTarget CoverageOption entity);

    // Custom mappings
    @AfterMapping
    default void linkCoverageOptions(@MappingTarget Quote quote) {
        if (quote.getCoverageOptions() != null) {
            quote.getCoverageOptions().forEach(option -> option.setQuote(quote));
        }
    }
}