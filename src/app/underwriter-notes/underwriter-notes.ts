import { Component, inject, effect, PLATFORM_ID } from '@angular/core';
import { CommonModule, isPlatformBrowser } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiQuoteService } from '../services/api-quote.service';

@Component({
  selector: 'app-underwriter-notes',
  imports: [CommonModule, FormsModule],
  templateUrl: './underwriter-notes.html',
  styleUrl: './underwriter-notes.css'
})
export class UnderwriterNotes {
  private apiQuoteService = inject(ApiQuoteService);
  private platformId = inject(PLATFORM_ID);
  
  notes = '';

  constructor() {
    if (isPlatformBrowser(this.platformId)) {
      effect(() => {
        const currentQuote = this.apiQuoteService.getCurrentQuote();
        if (currentQuote) {
          this.notes = currentQuote.underwriterNotes || '';
        }
      });
    }
  }

  onNotesChange(value: string) {
    this.notes = value;
    this.apiQuoteService.updateUnderwriterNotes(value);
  }
}
