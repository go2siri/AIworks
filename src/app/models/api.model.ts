export interface BusinessInformation {
  id?: number;
  name: string;
  businessType: BusinessType;
  industry: Industry;
  state: string;
  createdAt?: Date;
  updatedAt?: Date;
}

export interface CoverageOption {
  id?: number;
  name: string;
  coverageType: CoverageType;
  premium: number;
  description?: string;
  isActive?: boolean;
  isSelected?: boolean;
  createdAt?: Date;
  updatedAt?: Date;
}

export interface Quote {
  id?: number;
  businessInformation: BusinessInformation;
  coverageOptions: CoverageOption[];
  totalPremium: number;
  riskRating?: string;
  underwriterNotes?: string;
  status: QuoteStatus;
  quoteNumber?: string;
  validUntil?: Date;
  createdAt?: Date;
  updatedAt?: Date;
}

export enum BusinessType {
  RETAIL = 'RETAIL',
  RESTAURANT = 'RESTAURANT',
  TECHNOLOGY = 'TECHNOLOGY',
  MANUFACTURING = 'MANUFACTURING',
  HEALTHCARE = 'HEALTHCARE',
  PROFESSIONAL = 'PROFESSIONAL'
}

export enum Industry {
  FOOD_SERVICE = 'FOOD_SERVICE',
  RETAIL_TRADE = 'RETAIL_TRADE',
  SOFTWARE = 'SOFTWARE',
  HEALTHCARE_SERVICES = 'HEALTHCARE_SERVICES',
  CONSULTING = 'CONSULTING',
  MANUFACTURING = 'MANUFACTURING'
}

export enum CoverageType {
  GENERAL_LIABILITY = 'GENERAL_LIABILITY',
  PROPERTY = 'PROPERTY',
  ADDITIONAL = 'ADDITIONAL'
}

export enum QuoteStatus {
  DRAFT = 'DRAFT',
  SAVED = 'SAVED',
  SUBMITTED = 'SUBMITTED',
  APPROVED = 'APPROVED',
  REJECTED = 'REJECTED',
  EXPIRED = 'EXPIRED'
}

export interface ApiResponse<T> {
  data?: T;
  message?: string;
  status: number;
  timestamp: Date;
  path?: string;
  validationErrors?: Record<string, string>;
}

export interface QuoteStatistics {
  totalQuotes: number;
  draftQuotes: number;
  savedQuotes: number;
  submittedQuotes: number;
  approvedQuotes: number;
  rejectedQuotes: number;
  expiredQuotes: number;
  totalPremiumValue: number;
  averagePremium: number;
}