import { Component, OnInit, inject, effect, PLATFORM_ID, ChangeDetectorRef } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { CommonModule, isPlatformBrowser } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import { ApiQuoteService } from '../services/api-quote.service';
import { BusinessType, Industry } from '../models/api.model';
import { debounceTime } from 'rxjs/operators';

@Component({
  selector: 'app-business-information',
  imports: [ReactiveFormsModule, CommonModule, HttpClientModule],
  templateUrl: './business-information.html',
  styleUrl: './business-information.css'
})
export class BusinessInformation implements OnInit {
  private fb = inject(FormBuilder);
  private apiQuoteService = inject(ApiQuoteService);
  private platformId = inject(PLATFORM_ID);
  private cdr = inject(ChangeDetectorRef);
  
  businessForm!: FormGroup;
  errors: string[] = [];

  businessTypes = [
    { value: 'RETAIL', label: 'Retail' },
    { value: 'RESTAURANT', label: 'Restaurant' },
    { value: 'TECHNOLOGY', label: 'Technology' },
    { value: 'MANUFACTURING', label: 'Manufacturing' },
    { value: 'HEALTHCARE', label: 'Healthcare' },
    { value: 'PROFESSIONAL', label: 'Professional Services' }
  ];

  industries = [
    { value: 'FOOD_SERVICE', label: 'Food Service' },
    { value: 'RETAIL_TRADE', label: 'Retail Trade' },
    { value: 'SOFTWARE', label: 'Software Development' },
    { value: 'HEALTHCARE_SERVICES', label: 'Healthcare Services' },
    { value: 'CONSULTING', label: 'Consulting' },
    { value: 'MANUFACTURING', label: 'Manufacturing' }
  ];

  constructor() {
    // Removed conflicting effect that was interfering with form input
  }

  ngOnInit() {
    this.initializeForm();
    this.loadCurrentQuote();
  }

  initializeForm() {
    this.businessForm = this.fb.group({
      name: ['', [Validators.required, Validators.minLength(2)]],
      businessType: ['', Validators.required],
      industry: ['', Validators.required],
      state: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(2)]]
    });

    // Update service when form changes
    this.businessForm.valueChanges.subscribe(value => {
      console.log('Form values changed:', value);
      
      // Auto-uppercase state
      if (value.state) {
        value.state = value.state.toUpperCase();
        this.businessForm.patchValue({ state: value.state }, { emitEvent: false });
      }
      
      // Update service
      this.apiQuoteService.updateBusinessInfo(value);
      console.log('Updated service with:', value);
    });
  }

  loadCurrentQuote() {
    // Subscribe to quote changes to update form
    this.apiQuoteService.currentQuote$.subscribe(quote => {
      if (quote?.businessInformation) {
        this.businessForm.patchValue(quote.businessInformation, { emitEvent: false });
      }
    });
  }

  getFormValidationErrors(): string[] {
    const errors: string[] = [];
    Object.keys(this.businessForm.controls).forEach(key => {
      const control = this.businessForm.get(key);
      if (control && control.errors && control.touched) {
        switch (key) {
          case 'name':
            if (control.errors['required']) errors.push('Business name is required');
            if (control.errors['minlength']) errors.push('Business name must be at least 2 characters');
            if (control.errors['maxlength']) errors.push('Business name must be less than 100 characters');
            break;
          case 'businessType':
            if (control.errors['required']) errors.push('Business type is required');
            break;
          case 'industry':
            if (control.errors['required']) errors.push('Industry is required');
            break;
          case 'state':
            if (control.errors['required']) errors.push('State is required');
            if (control.errors['pattern']) errors.push('State must be a 2-letter code (e.g., CA, NY)');
            break;
        }
      }
    });
    return errors;
  }

  private markFormGroupTouched(formGroup: FormGroup) {
    Object.keys(formGroup.controls).forEach(key => {
      const control = formGroup.get(key);
      control?.markAsTouched();
    });
  }

  get nameError() {
    const control = this.businessForm.get('name');
    if (control?.touched && control?.errors) {
      if (control.errors['required']) return 'Business name is required';
      if (control.errors['minlength']) return 'Business name must be at least 2 characters';
    }
    return '';
  }

  get stateError() {
    const control = this.businessForm.get('state');
    if (control?.touched && control?.errors) {
      if (control.errors['required']) return 'State is required';
      if (control.errors['pattern']) return 'State must be a 2-letter code (e.g., CA, NY)';
    }
    return '';
  }
}