import { Injectable, inject, PLATFORM_ID } from '@angular/core';
import { HttpClient, HttpParams, HttpErrorResponse } from '@angular/common/http';
import { isPlatformBrowser } from '@angular/common';
import { Observable, throwError, BehaviorSubject } from 'rxjs';
import { catchError, tap, map } from 'rxjs/operators';
import { Quote, QuoteStatus, QuoteStatistics } from '../models/api.model';

@Injectable({
  providedIn: 'root'
})
export class ApiQuoteService {
  private platformId = inject(PLATFORM_ID);
  private http = inject(HttpClient);
  
  private readonly baseUrl = 'http://localhost:8080/api/quotes';
  private currentQuoteSubject = new BehaviorSubject<Quote | null>(null);
  public currentQuote$ = this.currentQuoteSubject.asObservable();

  constructor() {
    // Always create a default quote - simple and works everywhere
    const defaultQuote = this.createDefaultQuote();
    this.currentQuoteSubject.next(defaultQuote);
  }
  
  private createDefaultQuote(): Quote {
    return {
      businessInformation: {
        name: '',
        businessType: 'RETAIL' as any,
        industry: 'RETAIL_TRADE' as any,
        state: ''
      },
      coverageOptions: [
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
      ],
      totalPremium: 0,
      status: 'DRAFT' as any
    };
  }

  /**
   * Create a new quote
   */
  createQuote(quote: Quote): Observable<Quote> {
    console.log('Creating quote:', quote);
    return this.http.post<Quote>(this.baseUrl, quote).pipe(
      tap(createdQuote => {
        console.log('Quote created successfully:', createdQuote);
        this.setCurrentQuote(createdQuote);
      }),
      catchError(this.handleError)
    );
  }

  /**
   * Update an existing quote
   */
  updateQuote(id: number, quote: Quote): Observable<Quote> {
    console.log('Updating quote:', id, quote);
    return this.http.put<Quote>(`${this.baseUrl}/${id}`, quote).pipe(
      tap(updatedQuote => {
        console.log('Quote updated successfully:', updatedQuote);
        this.setCurrentQuote(updatedQuote);
      }),
      catchError(this.handleError)
    );
  }

  /**
   * Get quote by ID
   */
  getQuoteById(id: number): Observable<Quote> {
    console.log('Getting quote by ID:', id);
    return this.http.get<Quote>(`${this.baseUrl}/${id}`).pipe(
      tap(quote => {
        console.log('Quote retrieved:', quote);
        this.setCurrentQuote(quote);
      }),
      catchError(this.handleError)
    );
  }

  /**
   * Get quote by quote number
   */
  getQuoteByNumber(quoteNumber: string): Observable<Quote> {
    console.log('Getting quote by number:', quoteNumber);
    return this.http.get<Quote>(`${this.baseUrl}/number/${quoteNumber}`).pipe(
      tap(quote => {
        console.log('Quote retrieved by number:', quote);
        this.setCurrentQuote(quote);
      }),
      catchError(this.handleError)
    );
  }

  /**
   * Get all quotes with pagination
   */
  getAllQuotes(page: number = 0, size: number = 20, sort: string = 'createdAt,desc'): Observable<any> {
    console.log('Getting all quotes:', { page, size, sort });
    
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString())
      .set('sort', sort);

    return this.http.get<any>(this.baseUrl, { params }).pipe(
      tap(response => console.log('Quotes retrieved:', response)),
      catchError(this.handleError)
    );
  }

  /**
   * Get quotes by status
   */
  getQuotesByStatus(status: QuoteStatus): Observable<Quote[]> {
    console.log('Getting quotes by status:', status);
    return this.http.get<Quote[]>(`${this.baseUrl}/status/${status}`).pipe(
      tap(quotes => console.log('Quotes by status retrieved:', quotes)),
      catchError(this.handleError)
    );
  }

  /**
   * Search quotes by business name
   */
  searchQuotesByBusinessName(businessName: string, page: number = 0, size: number = 20): Observable<any> {
    console.log('Searching quotes by business name:', businessName);
    
    let params = new HttpParams()
      .set('businessName', businessName)
      .set('page', page.toString())
      .set('size', size.toString());

    return this.http.get<any>(`${this.baseUrl}/search`, { params }).pipe(
      tap(response => console.log('Search results:', response)),
      catchError(this.handleError)
    );
  }

  /**
   * Get quotes by state
   */
  getQuotesByState(state: string): Observable<Quote[]> {
    console.log('Getting quotes by state:', state);
    return this.http.get<Quote[]>(`${this.baseUrl}/state/${state}`).pipe(
      tap(quotes => console.log('Quotes by state retrieved:', quotes)),
      catchError(this.handleError)
    );
  }

  /**
   * Delete a quote
   */
  deleteQuote(id: number): Observable<void> {
    console.log('Deleting quote:', id);
    return this.http.delete<void>(`${this.baseUrl}/${id}`).pipe(
      tap(() => {
        console.log('Quote deleted successfully');
        if (this.currentQuoteSubject.value?.id === id) {
          this.setCurrentQuote(null);
        }
      }),
      catchError(this.handleError)
    );
  }

  /**
   * Calculate quote premium
   */
  calculateQuotePremium(id: number): Observable<{ totalPremium: number }> {
    console.log('Calculating premium for quote:', id);
    return this.http.get<{ totalPremium: number }>(`${this.baseUrl}/${id}/premium`).pipe(
      tap(result => console.log('Premium calculated:', result)),
      catchError(this.handleError)
    );
  }

  /**
   * Submit quote for approval
   */
  submitQuote(id: number): Observable<Quote> {
    console.log('Submitting quote:', id);
    return this.http.post<Quote>(`${this.baseUrl}/${id}/submit`, {}).pipe(
      tap(submittedQuote => {
        console.log('Quote submitted successfully:', submittedQuote);
        this.setCurrentQuote(submittedQuote);
      }),
      catchError(this.handleError)
    );
  }

  /**
   * Approve a quote
   */
  approveQuote(id: number): Observable<Quote> {
    console.log('Approving quote:', id);
    return this.http.post<Quote>(`${this.baseUrl}/${id}/approve`, {}).pipe(
      tap(approvedQuote => {
        console.log('Quote approved successfully:', approvedQuote);
        this.setCurrentQuote(approvedQuote);
      }),
      catchError(this.handleError)
    );
  }

  /**
   * Reject a quote
   */
  rejectQuote(id: number, reason: string): Observable<Quote> {
    console.log('Rejecting quote:', id, 'with reason:', reason);
    
    let params = new HttpParams().set('reason', reason);
    
    return this.http.post<Quote>(`${this.baseUrl}/${id}/reject`, {}, { params }).pipe(
      tap(rejectedQuote => {
        console.log('Quote rejected successfully:', rejectedQuote);
        this.setCurrentQuote(rejectedQuote);
      }),
      catchError(this.handleError)
    );
  }

  /**
   * Get quote statistics
   */
  getQuoteStatistics(): Observable<QuoteStatistics> {
    console.log('Getting quote statistics');
    return this.http.get<QuoteStatistics>(`${this.baseUrl}/statistics`).pipe(
      tap(stats => console.log('Quote statistics retrieved:', stats)),
      catchError(this.handleError)
    );
  }

  /**
   * Check if quote number is unique
   */
  checkQuoteNumber(quoteNumber: string): Observable<{ isUnique: boolean }> {
    console.log('Checking quote number uniqueness:', quoteNumber);
    return this.http.get<{ isUnique: boolean }>(`${this.baseUrl}/check-number/${quoteNumber}`).pipe(
      tap(result => console.log('Quote number check result:', result)),
      catchError(this.handleError)
    );
  }

  /**
   * Get current quote
   */
  getCurrentQuote(): Quote | null {
    return this.currentQuoteSubject.value;
  }

  /**
   * Set current quote
   */
  setCurrentQuote(quote: Quote | null): void {
    this.currentQuoteSubject.next(quote);
  }

  /**
   * Create a new draft quote
   */
  createNewQuote(): Observable<Quote> {
    const newQuote = this.createDefaultQuote();
    this.setCurrentQuote(newQuote);
    return new Observable(observer => {
      observer.next(newQuote);
      observer.complete();
    });
  }

  /**
   * Update business information
   */
  updateBusinessInfo(businessInfo: any): void {
    const currentQuote = this.getCurrentQuote();
    if (currentQuote) {
      currentQuote.businessInformation = { ...currentQuote.businessInformation, ...businessInfo };
      this.currentQuoteSubject.next(currentQuote);
    }
  }

  /**
   * Update coverage options
   */
  updateCoverageOptions(options: any[]): void {
    const currentQuote = this.getCurrentQuote();
    if (currentQuote) {
      currentQuote.coverageOptions = [...options];
      const selectedOptions = options.filter(option => option.isSelected);
      const newTotalPremium = selectedOptions
        .reduce((total, option) => total + (option.premium || 0), 0);
      
      currentQuote.totalPremium = newTotalPremium;
      
      console.log('🔄 Service: Updated coverage options');
      console.log('📊 Service: Selected options:', selectedOptions.map(o => ({name: o.name, premium: o.premium, selected: o.isSelected})));
      console.log('💰 Service: New total premium:', newTotalPremium);
      console.log('📋 Service: Updated quote:', currentQuote);
      
      // Force update the subject to trigger all subscribers
      this.currentQuoteSubject.next({...currentQuote});
    }
  }

  /**
   * Update underwriter notes
   */
  updateUnderwriterNotes(notes: string): void {
    const currentQuote = this.getCurrentQuote();
    if (currentQuote) {
      currentQuote.underwriterNotes = notes;
      this.setCurrentQuote(currentQuote);
    }
  }

  /**
   * Save quote (persist to backend) - creates new or updates existing
   */
  saveCurrentQuote(): Observable<Quote> {
    const currentQuote = this.getCurrentQuote();
    console.log('🔄 saveCurrentQuote called with:', currentQuote);
    
    if (!currentQuote) {
      console.error('❌ No current quote to save');
      return throwError(() => new Error('No current quote to save'));
    }

    // Clean quote data - remove any IDs to avoid detached entity issues
    const cleanBusinessInfo = {
      name: currentQuote.businessInformation?.name || '',
      businessType: currentQuote.businessInformation?.businessType || 'RETAIL',
      industry: currentQuote.businessInformation?.industry || 'RETAIL_TRADE',
      state: currentQuote.businessInformation?.state || ''
    };

    const cleanCoverageOptions = (currentQuote.coverageOptions || []).map(option => ({
      name: option.name,
      coverageType: option.coverageType,
      premium: option.premium,
      description: option.description,
      isActive: option.isActive,
      isSelected: option.isSelected
    }));

    const quoteToSave = { 
      businessInformation: cleanBusinessInfo,
      coverageOptions: cleanCoverageOptions,
      totalPremium: currentQuote.totalPremium || 0,
      status: currentQuote.status || 'DRAFT' as any
    };

    console.log('🚀 Sending to backend:', quoteToSave);

    // Check if this is an existing quote (has ID) or new quote
    const isExisting = currentQuote.id && currentQuote.id > 0;
    console.log(`📝 Quote operation: ${isExisting ? 'UPDATE' : 'CREATE'} (ID: ${currentQuote.id})`);

    const saveOperation = isExisting 
      ? this.updateQuote(currentQuote.id!, quoteToSave)
      : this.createQuote(quoteToSave);

    return saveOperation.pipe(
      tap(savedQuote => {
        console.log(`✅ Backend ${isExisting ? 'update' : 'create'} successful:`, savedQuote);
        this.setCurrentQuote(savedQuote);
      }),
      catchError(error => {
        console.error(`❌ Backend ${isExisting ? 'update' : 'create'} failed:`, error);
        
        // Check if it's a detached entity error but the save might have succeeded
        if (error.message && error.message.includes('detached entity')) {
          console.log('🔍 Detached entity error detected - checking if save actually succeeded...');
          // Return a more user-friendly error but note that save might have worked
          return throwError(() => new Error('Quote may have been saved successfully. Please refresh the quote list to verify.'));
        }
        
        return throwError(() => error);
      })
    );
  }

  /**
   * Validate business information
   */
  validateBusinessInfo(businessInfo: any): { valid: boolean; errors: string[] } {
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
    
    if (!businessInfo.state || !/^[A-Z]{2}$/.test(businessInfo.state)) {
      errors.push('State must be a 2-letter code');
    }
    
    return {
      valid: errors.length === 0,
      errors
    };
  }

  // Private helper methods
  private handleError(error: HttpErrorResponse): Observable<never> {
    console.error('API Error occurred:', error);
    
    let errorMessage = 'An unexpected error occurred';
    
    if (error.error) {
      if (typeof error.error === 'string') {
        errorMessage = error.error;
      } else if (error.error.message) {
        errorMessage = error.error.message;
      } else if (error.error.validationErrors) {
        errorMessage = Object.values(error.error.validationErrors).join(', ');
      }
    } else if (error.message) {
      errorMessage = error.message;
    }
    
    console.error('Error message:', errorMessage);
    return throwError(() => new Error(errorMessage));
  }

  private saveCurrentQuoteToStorage(quote: Quote | null): void {
    try {
      if (quote) {
        localStorage.setItem('currentQuote', JSON.stringify(quote));
      } else {
        localStorage.removeItem('currentQuote');
      }
    } catch (error) {
      console.warn('Failed to save quote to localStorage:', error);
    }
  }

  private loadCurrentQuoteFromStorage(): void {
    try {
      const savedQuote = localStorage.getItem('currentQuote');
      if (savedQuote) {
        const quote = JSON.parse(savedQuote);
        this.currentQuoteSubject.next(quote);
      }
    } catch (error) {
      console.warn('Failed to load quote from localStorage:', error);
    }
  }

  private saveQuoteToLocalStorage(quote: Quote): void {
    try {
      // Save to allQuotes list
      const allQuotesStr = localStorage.getItem('allQuotes');
      let allQuotes: Quote[] = allQuotesStr ? JSON.parse(allQuotesStr) : [];
      
      // Check if quote already exists
      const existingIndex = allQuotes.findIndex(q => q.id === quote.id);
      if (existingIndex >= 0) {
        allQuotes[existingIndex] = quote;
      } else {
        allQuotes.push(quote);
      }
      
      localStorage.setItem('allQuotes', JSON.stringify(allQuotes));
    } catch (error) {
      console.warn('Failed to save quote to localStorage:', error);
    }
  }

}