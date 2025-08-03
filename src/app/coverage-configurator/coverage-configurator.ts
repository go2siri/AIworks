import { Component, inject, effect, PLATFORM_ID, ChangeDetectorRef, OnInit } from '@angular/core';
import { CommonModule, isPlatformBrowser } from '@angular/common';
import { ApiQuoteService } from '../services/api-quote.service';
import { CoverageOption } from '../models/api.model';

@Component({
  selector: 'app-coverage-configurator',
  imports: [CommonModule],
  templateUrl: './coverage-configurator.html',
  styleUrl: './coverage-configurator.css'
})
export class CoverageConfigurator implements OnInit {
  private apiQuoteService = inject(ApiQuoteService);
  private platformId = inject(PLATFORM_ID);
  private cdr = inject(ChangeDetectorRef);
  
  generalLiabilityExpanded = false;
  propertyExpanded = false;
  additionalExpanded = false;
  
  coverageOptions: CoverageOption[] = [];

  constructor() {
    // Always initialize default coverage options
    this.initializeDefaultCoverageOptions();
    
    if (isPlatformBrowser(this.platformId)) {
      effect(() => {
        const currentQuote = this.apiQuoteService.getCurrentQuote();
        if (currentQuote && currentQuote.coverageOptions.length > 0) {
          this.coverageOptions = [...currentQuote.coverageOptions];
          this.cdr.markForCheck();
        }
      });
    }
  }

  ngOnInit() {
    // Ensure coverage options are available and update service
    if (this.coverageOptions.length > 0) {
      console.log('Initializing coverage options:', this.coverageOptions);
      this.apiQuoteService.updateCoverageOptions(this.coverageOptions);
    } else {
      console.log('No coverage options found on init');
    }
  }

  private initializeDefaultCoverageOptions() {
    this.coverageOptions = [
      {
        name: 'General Liability',
        coverageType: 'GENERAL_LIABILITY' as any,
        premium: 500,
        description: 'General liability insurance protects your business from claims of bodily injury, property damage, and personal injury.',
        isActive: true,
        isSelected: false
      },
      {
        name: 'Property Insurance',
        coverageType: 'PROPERTY' as any,
        premium: 750,
        description: 'Property insurance covers your business property including buildings, equipment, inventory, and furniture against damage or theft.',
        isActive: true,
        isSelected: false
      },
      {
        name: 'Additional Coverage Options',
        coverageType: 'ADDITIONAL' as any,
        premium: 300,
        description: 'Additional coverage options include cyber liability, employment practices liability, and other specialized coverages.',
        isActive: true,
        isSelected: false
      }
    ];
  }

  toggleGeneralLiability() {
    this.generalLiabilityExpanded = !this.generalLiabilityExpanded;
  }

  toggleProperty() {
    this.propertyExpanded = !this.propertyExpanded;
  }

  toggleAdditional() {
    this.additionalExpanded = !this.additionalExpanded;
  }

  onCoverageToggle(optionId: string) {
    const option = this.getCoverageOption(optionId);
    if (option) {
      option.isSelected = !option.isSelected;
      console.log(`Coverage ${optionId} toggled to:`, option.isSelected, 'Premium:', option.premium);
      
      // Calculate total premium from selected options
      const selectedOptions = this.coverageOptions.filter(opt => opt.isSelected);
      const totalPremium = selectedOptions.reduce((total, opt) => total + (opt.premium || 0), 0);
      
      console.log('Selected coverages:', selectedOptions.map(opt => `${opt.name}: $${opt.premium}`));
      console.log('New total premium:', totalPremium);
      
      // Update the service with new coverage options AND total premium
      this.apiQuoteService.updateCoverageOptions(this.coverageOptions);
      
      // Force change detection to update UI immediately
      this.cdr.detectChanges();
    } else {
      console.error(`Coverage option not found for ID: ${optionId}`);
      console.log('Available coverage options:', this.coverageOptions.map(opt => ({
        name: opt.name,
        type: opt.coverageType,
        mapped: opt.coverageType.toString().toLowerCase().replace('_', '-')
      })));
    }
  }

  getCoverageOption(id: string): CoverageOption | undefined {
    // Map the HTML IDs to coverage types
    const typeMap: Record<string, string> = {
      'general-liability': 'GENERAL_LIABILITY',
      'property': 'PROPERTY', 
      'additional': 'ADDITIONAL'
    };
    
    const coverageType = typeMap[id];
    if (!coverageType) {
      console.error(`No mapping found for coverage ID: ${id}`);
      return undefined;
    }
    
    return this.coverageOptions.find(opt => opt.coverageType.toString() === coverageType);
  }
}