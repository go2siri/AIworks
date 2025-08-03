import { Component, inject, OnInit, OnDestroy, signal, PLATFORM_ID } from '@angular/core';
import { CommonModule, isPlatformBrowser } from '@angular/common';
import { ApiQuoteService } from '../services/api-quote.service';
import { CoverageOption } from '../models/api.model';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-quote-summary',
  imports: [CommonModule],
  templateUrl: './quote-summary.html',
  styleUrl: './quote-summary.css'
})
export class QuoteSummary implements OnInit, OnDestroy {
  private apiQuoteService = inject(ApiQuoteService);
  private platformId = inject(PLATFORM_ID);
  private subscription?: Subscription;
  
  totalPremium = signal(0);
  quoteStatus = signal('draft');
  selectedCoverages = signal<CoverageOption[]>([]);

  ngOnInit() {
    if (isPlatformBrowser(this.platformId)) {
      // Subscribe to the observable instead of using effect
      this.subscription = this.apiQuoteService.currentQuote$.subscribe(currentQuote => {
        if (currentQuote) {
          console.log('🎯 Quote Summary Subscription - Current quote received:', currentQuote);
          console.log('🎯 Quote Summary Subscription - Total premium from quote:', currentQuote.totalPremium);
          
          const newPremium = currentQuote.totalPremium || 0;
          const newStatus = currentQuote.status || 'draft';
          const selected = currentQuote.coverageOptions?.filter(opt => opt.isSelected) || [];
          
          console.log('🎯 Quote Summary Subscription - Setting premium to:', newPremium);
          console.log('🎯 Quote Summary Subscription - Selected coverages:', selected.map(c => ({name: c.name, premium: c.premium})));
          
          this.totalPremium.set(newPremium);
          this.quoteStatus.set(newStatus);
          this.selectedCoverages.set(selected);
          
          console.log('🎯 Quote Summary Subscription - Updated signals');
          console.log('🎯 Quote Summary Subscription - totalPremium signal value:', this.totalPremium());
          console.log('🎯 Quote Summary Subscription - selectedCoverages signal value:', this.selectedCoverages());
        }
      });
    }
  }

  ngOnDestroy() {
    if (this.subscription) {
      this.subscription.unsubscribe();
    }
  }
}