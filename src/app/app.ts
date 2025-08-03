import { Component } from '@angular/core';
import { HttpClientModule } from '@angular/common/http';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { BusinessInformation } from './business-information/business-information';
import { QuoteSummary } from './quote-summary/quote-summary';
import { RiskRatingSnapshot } from './risk-rating-snapshot/risk-rating-snapshot';
import { CoverageConfigurator } from './coverage-configurator/coverage-configurator';
import { UnderwriterNotes } from './underwriter-notes/underwriter-notes';
import { ApiQuoteService } from './services/api-quote.service';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-root',
  imports: [
    HttpClientModule,
    CommonModule,
    FormsModule,
    BusinessInformation,
    QuoteSummary,
    RiskRatingSnapshot,
    CoverageConfigurator,
    UnderwriterNotes
  ],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {
  title = 'aiprojects';
  message = '';
  quotes: any[] = [];
  showQuotes = false;
  currentQuote: any = null;

  constructor(private apiService: ApiQuoteService, private http: HttpClient) {
    // Load quotes on startup
    this.loadQuotes();
    
    // Subscribe to current quote changes
    this.apiService.currentQuote$.subscribe(quote => {
      this.currentQuote = quote;
    });
  }

  get isExistingQuote(): boolean {
    return this.currentQuote && this.currentQuote.id && this.currentQuote.id > 0;
  }

  // Save quote
  saveQuote() {
    const isUpdate = this.isExistingQuote;
    console.log(`💾 ${isUpdate ? 'Update' : 'Save'} clicked`);
    this.message = isUpdate ? 'Updating quote...' : 'Saving quote...';
    
    const quote = this.apiService.getCurrentQuote();
    console.log('Quote to save:', quote);
    
    // Validate business info before saving
    if (!quote?.businessInformation?.name) {
      this.message = '⚠️ Please enter a business name to save your quote';
      return;
    }
    
    if (!quote?.businessInformation?.businessType) {
      this.message = '⚠️ Please select a business type to continue';
      return;
    }
    
    if (!quote?.businessInformation?.industry) {
      this.message = '⚠️ Please select an industry for your business';
      return;
    }
    
    if (!quote?.businessInformation?.state) {
      this.message = '⚠️ Please enter your state (2-letter code like CA, NY)';
      return;
    }
    
    this.apiService.saveCurrentQuote().subscribe({
      next: (saved) => {
        console.log('✅ Saved:', saved);
        const action = isUpdate ? 'updated' : 'saved';
        this.message = `🎉 Quote successfully ${action}! Quote #${saved.id} is ready for review.`;
        this.loadQuotes();
      },
      error: (err) => {
        console.error('❌ Save error:', err);
        
        if (err.message && err.message.includes('Quote may have been saved successfully')) {
          this.message = `⚠️ ${err.message}`;
          // Auto-refresh the quotes list since save might have worked
          this.loadQuotes();
        } else {
          this.message = `❌ Unable to save quote. Please check your information and try again. ${err.message}`;
        }
      }
    });
  }

  // New quote
  newQuote() {
    console.log('🆕 New quote clicked');
    this.message = 'Preparing new quote form...';
    
    this.apiService.createNewQuote().subscribe({
      next: (newQuote) => {
        console.log('✅ New quote:', newQuote);
        this.message = '📝 New quote form is ready! Please fill in your business information.';
      },
      error: (err) => {
        console.error('❌ New quote error:', err);
        this.message = '❌ Unable to create new quote. Please try again.';
      }
    });
  }

  // Toggle quotes list
  toggleQuotes() {
    console.log('📋 Toggle quotes clicked');
    this.showQuotes = !this.showQuotes;
    if (this.showQuotes) {
      this.loadQuotes();
    }
  }

  // Load quotes from backend
  loadQuotes() {
    console.log('🔄 Loading quotes...');
    this.apiService.getAllQuotes().subscribe({
      next: (response) => {
        console.log('📋 Response:', response);
        this.quotes = response?.content || [];
        console.log('📋 Quotes set:', this.quotes);
      },
      error: (err) => {
        console.error('❌ Load quotes error:', err);
        this.quotes = [];
      }
    });
  }

  // Edit quote
  editQuote(quote: any) {
    console.log('✏️ Edit quote:', quote);
    this.apiService.setCurrentQuote(quote);
    this.showQuotes = false;
    this.message = `📝 Quote #${quote.id} loaded for editing. Make your changes and save when ready.`;
  }
}
