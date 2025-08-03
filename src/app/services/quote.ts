import { Injectable, signal, PLATFORM_ID, inject } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { Quote, BusinessInformation, CoverageOption } from '../models/quote.model';

@Injectable({
  providedIn: 'root'
})
export class QuoteService {
  private platformId = inject(PLATFORM_ID);
  private quotes = signal<Quote[]>([]);
  private currentQuote = signal<Quote | null>(null);
  
  constructor() {
    // Load quotes from localStorage on initialization (only in browser)
    if (isPlatformBrowser(this.platformId)) {
      this.loadFromStorage();
    }
  }

  private loadFromStorage() {
    try {
      const savedQuotes = localStorage.getItem('quotes');
      if (savedQuotes) {
        this.quotes.set(JSON.parse(savedQuotes));
      }
    } catch (error) {
      console.warn('Failed to load quotes from localStorage:', error);
    }
  }

  // Get all quotes
  getAllQuotes() {
    return this.quotes();
  }

  // Get current quote
  getCurrentQuote() {
    return this.currentQuote();
  }

  // Create new quote
  createNewQuote(): Quote {
    const newQuote: Quote = {
      businessInfo: {
        name: '',
        businessType: '',
        industry: '',
        state: ''
      },
      coverageOptions: [
        { id: 'general-liability', name: 'General Liability', selected: false, premium: 500 },
        { id: 'property', name: 'Property', selected: false, premium: 750 },
        { id: 'additional', name: 'Additional Coverage Options', selected: false, premium: 300 }
      ],
      totalPremium: 0,
      status: 'draft',
      createdAt: new Date(),
      updatedAt: new Date()
    };
    
    this.currentQuote.set(newQuote);
    return newQuote;
  }

  // Save quote
  saveQuote(quote: Quote): Quote {
    if (!quote.id) {
      quote.id = this.generateId();
      quote.createdAt = new Date();
    }
    
    quote.updatedAt = new Date();
    quote.status = 'saved';
    
    // Calculate total premium
    quote.totalPremium = quote.coverageOptions
      .filter(option => option.selected)
      .reduce((total, option) => total + (option.premium || 0), 0);
    
    const quotes = this.quotes();
    const existingIndex = quotes.findIndex(q => q.id === quote.id);
    
    if (existingIndex >= 0) {
      quotes[existingIndex] = quote;
    } else {
      quotes.push(quote);
    }
    
    this.quotes.set([...quotes]);
    this.saveToLocalStorage();
    this.currentQuote.set(quote);
    
    return quote;
  }

  // Update business information
  updateBusinessInfo(businessInfo: BusinessInformation) {
    const current = this.currentQuote();
    if (current) {
      current.businessInfo = businessInfo;
      this.currentQuote.set({...current});
    }
  }

  // Update coverage options
  updateCoverageOptions(options: CoverageOption[]) {
    const current = this.currentQuote();
    if (current) {
      current.coverageOptions = options;
      current.totalPremium = options
        .filter(option => option.selected)
        .reduce((total, option) => total + (option.premium || 0), 0);
      this.currentQuote.set({...current});
    }
  }

  // Load quote by ID
  loadQuote(id: string): Quote | null {
    const quote = this.quotes().find(q => q.id === id);
    if (quote) {
      this.currentQuote.set(quote);
      return quote;
    }
    return null;
  }

  // Delete quote
  deleteQuote(id: string): boolean {
    const quotes = this.quotes().filter(q => q.id !== id);
    this.quotes.set(quotes);
    this.saveToLocalStorage();
    
    if (this.currentQuote()?.id === id) {
      this.currentQuote.set(null);
    }
    
    return true;
  }

  // Validate business information
  validateBusinessInfo(businessInfo: BusinessInformation): { valid: boolean; errors: string[] } {
    const errors: string[] = [];
    
    if (!businessInfo.name || businessInfo.name.trim().length < 2) {
      errors.push('Business name must be at least 2 characters long');
    }
    
    if (!businessInfo.businessType) {
      errors.push('Business type is required');
    }
    
    if (!businessInfo.industry) {
      errors.push('Industry is required');
    }
    
    if (!businessInfo.state || businessInfo.state.length !== 2) {
      errors.push('State must be a 2-letter code');
    }
    
    return {
      valid: errors.length === 0,
      errors
    };
  }

  // Private helper methods
  private generateId(): string {
    return Date.now().toString(36) + Math.random().toString(36).substr(2);
  }

  private saveToLocalStorage() {
    if (isPlatformBrowser(this.platformId)) {
      try {
        localStorage.setItem('quotes', JSON.stringify(this.quotes()));
      } catch (error) {
        console.warn('Failed to save quotes to localStorage:', error);
      }
    }
  }
}