package com.insurance.quote.mapper;

import com.insurance.quote.dto.BusinessInformationDto;
import com.insurance.quote.dto.CoverageOptionDto;
import com.insurance.quote.dto.QuoteDto;
import com.insurance.quote.entity.BusinessInformation;
import com.insurance.quote.entity.CoverageOption;
import com.insurance.quote.entity.Quote;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-08-03T01:04:16+0000",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.2 (Oracle Corporation)"
)
@Component
public class QuoteMapperImpl implements QuoteMapper {

    @Override
    public QuoteDto toDto(Quote quote) {
        if ( quote == null ) {
            return null;
        }

        QuoteDto quoteDto = new QuoteDto();

        quoteDto.setId( quote.getId() );
        quoteDto.setBusinessInformation( toDto( quote.getBusinessInformation() ) );
        quoteDto.setCoverageOptions( toCoverageOptionDtoList( quote.getCoverageOptions() ) );
        quoteDto.setTotalPremium( quote.getTotalPremium() );
        quoteDto.setRiskRating( quote.getRiskRating() );
        quoteDto.setUnderwriterNotes( quote.getUnderwriterNotes() );
        quoteDto.setStatus( quote.getStatus() );
        quoteDto.setQuoteNumber( quote.getQuoteNumber() );
        quoteDto.setValidUntil( quote.getValidUntil() );
        quoteDto.setCreatedAt( quote.getCreatedAt() );
        quoteDto.setUpdatedAt( quote.getUpdatedAt() );

        return quoteDto;
    }

    @Override
    public Quote toEntity(QuoteDto quoteDto) {
        if ( quoteDto == null ) {
            return null;
        }

        Quote quote = new Quote();

        quote.setId( quoteDto.getId() );
        quote.setBusinessInformation( toEntity( quoteDto.getBusinessInformation() ) );
        quote.setCoverageOptions( toCoverageOptionEntityList( quoteDto.getCoverageOptions() ) );
        quote.setTotalPremium( quoteDto.getTotalPremium() );
        quote.setRiskRating( quoteDto.getRiskRating() );
        quote.setUnderwriterNotes( quoteDto.getUnderwriterNotes() );
        quote.setStatus( quoteDto.getStatus() );
        quote.setQuoteNumber( quoteDto.getQuoteNumber() );
        quote.setValidUntil( quoteDto.getValidUntil() );
        quote.setCreatedAt( quoteDto.getCreatedAt() );
        quote.setUpdatedAt( quoteDto.getUpdatedAt() );

        linkCoverageOptions( quote );

        return quote;
    }

    @Override
    public List<QuoteDto> toDtoList(List<Quote> quotes) {
        if ( quotes == null ) {
            return null;
        }

        List<QuoteDto> list = new ArrayList<QuoteDto>( quotes.size() );
        for ( Quote quote : quotes ) {
            list.add( toDto( quote ) );
        }

        return list;
    }

    @Override
    public List<Quote> toEntityList(List<QuoteDto> quoteDtos) {
        if ( quoteDtos == null ) {
            return null;
        }

        List<Quote> list = new ArrayList<Quote>( quoteDtos.size() );
        for ( QuoteDto quoteDto : quoteDtos ) {
            list.add( toEntity( quoteDto ) );
        }

        return list;
    }

    @Override
    public void updateEntityFromDto(QuoteDto dto, Quote entity) {
        if ( dto == null ) {
            return;
        }

        if ( dto.getBusinessInformation() != null ) {
            if ( entity.getBusinessInformation() == null ) {
                entity.setBusinessInformation( new BusinessInformation() );
            }
            updateEntityFromDto( dto.getBusinessInformation(), entity.getBusinessInformation() );
        }
        if ( entity.getCoverageOptions() != null ) {
            List<CoverageOption> list = toCoverageOptionEntityList( dto.getCoverageOptions() );
            if ( list != null ) {
                entity.getCoverageOptions().clear();
                entity.getCoverageOptions().addAll( list );
            }
        }
        else {
            List<CoverageOption> list = toCoverageOptionEntityList( dto.getCoverageOptions() );
            if ( list != null ) {
                entity.setCoverageOptions( list );
            }
        }
        if ( dto.getTotalPremium() != null ) {
            entity.setTotalPremium( dto.getTotalPremium() );
        }
        if ( dto.getRiskRating() != null ) {
            entity.setRiskRating( dto.getRiskRating() );
        }
        if ( dto.getUnderwriterNotes() != null ) {
            entity.setUnderwriterNotes( dto.getUnderwriterNotes() );
        }
        if ( dto.getStatus() != null ) {
            entity.setStatus( dto.getStatus() );
        }
        if ( dto.getQuoteNumber() != null ) {
            entity.setQuoteNumber( dto.getQuoteNumber() );
        }
        if ( dto.getValidUntil() != null ) {
            entity.setValidUntil( dto.getValidUntil() );
        }

        linkCoverageOptions( entity );
    }

    @Override
    public BusinessInformationDto toDto(BusinessInformation businessInformation) {
        if ( businessInformation == null ) {
            return null;
        }

        BusinessInformationDto businessInformationDto = new BusinessInformationDto();

        businessInformationDto.setId( businessInformation.getId() );
        businessInformationDto.setName( businessInformation.getName() );
        businessInformationDto.setBusinessType( businessInformation.getBusinessType() );
        businessInformationDto.setIndustry( businessInformation.getIndustry() );
        businessInformationDto.setState( businessInformation.getState() );
        businessInformationDto.setCreatedAt( businessInformation.getCreatedAt() );
        businessInformationDto.setUpdatedAt( businessInformation.getUpdatedAt() );

        return businessInformationDto;
    }

    @Override
    public BusinessInformation toEntity(BusinessInformationDto businessInformationDto) {
        if ( businessInformationDto == null ) {
            return null;
        }

        BusinessInformation businessInformation = new BusinessInformation();

        businessInformation.setId( businessInformationDto.getId() );
        businessInformation.setName( businessInformationDto.getName() );
        businessInformation.setBusinessType( businessInformationDto.getBusinessType() );
        businessInformation.setIndustry( businessInformationDto.getIndustry() );
        businessInformation.setState( businessInformationDto.getState() );
        businessInformation.setCreatedAt( businessInformationDto.getCreatedAt() );
        businessInformation.setUpdatedAt( businessInformationDto.getUpdatedAt() );

        return businessInformation;
    }

    @Override
    public void updateEntityFromDto(BusinessInformationDto dto, BusinessInformation entity) {
        if ( dto == null ) {
            return;
        }

        if ( dto.getName() != null ) {
            entity.setName( dto.getName() );
        }
        if ( dto.getBusinessType() != null ) {
            entity.setBusinessType( dto.getBusinessType() );
        }
        if ( dto.getIndustry() != null ) {
            entity.setIndustry( dto.getIndustry() );
        }
        if ( dto.getState() != null ) {
            entity.setState( dto.getState() );
        }
    }

    @Override
    public CoverageOptionDto toDto(CoverageOption coverageOption) {
        if ( coverageOption == null ) {
            return null;
        }

        CoverageOptionDto coverageOptionDto = new CoverageOptionDto();

        coverageOptionDto.setId( coverageOption.getId() );
        coverageOptionDto.setName( coverageOption.getName() );
        coverageOptionDto.setCoverageType( coverageOption.getCoverageType() );
        coverageOptionDto.setPremium( coverageOption.getPremium() );
        coverageOptionDto.setDescription( coverageOption.getDescription() );
        coverageOptionDto.setIsActive( coverageOption.getIsActive() );
        coverageOptionDto.setIsSelected( coverageOption.getIsSelected() );
        coverageOptionDto.setCreatedAt( coverageOption.getCreatedAt() );
        coverageOptionDto.setUpdatedAt( coverageOption.getUpdatedAt() );

        return coverageOptionDto;
    }

    @Override
    public CoverageOption toEntity(CoverageOptionDto coverageOptionDto) {
        if ( coverageOptionDto == null ) {
            return null;
        }

        CoverageOption coverageOption = new CoverageOption();

        coverageOption.setId( coverageOptionDto.getId() );
        coverageOption.setName( coverageOptionDto.getName() );
        coverageOption.setCoverageType( coverageOptionDto.getCoverageType() );
        coverageOption.setPremium( coverageOptionDto.getPremium() );
        coverageOption.setDescription( coverageOptionDto.getDescription() );
        coverageOption.setIsActive( coverageOptionDto.getIsActive() );
        coverageOption.setIsSelected( coverageOptionDto.getIsSelected() );
        coverageOption.setCreatedAt( coverageOptionDto.getCreatedAt() );
        coverageOption.setUpdatedAt( coverageOptionDto.getUpdatedAt() );

        return coverageOption;
    }

    @Override
    public List<CoverageOptionDto> toCoverageOptionDtoList(List<CoverageOption> coverageOptions) {
        if ( coverageOptions == null ) {
            return null;
        }

        List<CoverageOptionDto> list = new ArrayList<CoverageOptionDto>( coverageOptions.size() );
        for ( CoverageOption coverageOption : coverageOptions ) {
            list.add( toDto( coverageOption ) );
        }

        return list;
    }

    @Override
    public List<CoverageOption> toCoverageOptionEntityList(List<CoverageOptionDto> coverageOptionDtos) {
        if ( coverageOptionDtos == null ) {
            return null;
        }

        List<CoverageOption> list = new ArrayList<CoverageOption>( coverageOptionDtos.size() );
        for ( CoverageOptionDto coverageOptionDto : coverageOptionDtos ) {
            list.add( toEntity( coverageOptionDto ) );
        }

        return list;
    }

    @Override
    public void updateEntityFromDto(CoverageOptionDto dto, CoverageOption entity) {
        if ( dto == null ) {
            return;
        }

        if ( dto.getName() != null ) {
            entity.setName( dto.getName() );
        }
        if ( dto.getCoverageType() != null ) {
            entity.setCoverageType( dto.getCoverageType() );
        }
        if ( dto.getPremium() != null ) {
            entity.setPremium( dto.getPremium() );
        }
        if ( dto.getDescription() != null ) {
            entity.setDescription( dto.getDescription() );
        }
        if ( dto.getIsActive() != null ) {
            entity.setIsActive( dto.getIsActive() );
        }
        if ( dto.getIsSelected() != null ) {
            entity.setIsSelected( dto.getIsSelected() );
        }
    }
}
