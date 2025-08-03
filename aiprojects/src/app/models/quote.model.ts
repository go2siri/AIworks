export interface BusinessInformation {
  id?: string;
  name: string;
  businessType: string;
  industry: string;
  state: string;
}

export interface CoverageOption {
  id: string;
  name: string;
  selected: boolean;
  premium?: number;
}

export interface Quote {
  id?: string;
  businessInfo: BusinessInformation;
  coverageOptions: CoverageOption[];
  totalPremium: number;
  riskRating?: string;
  underwriterNotes?: string;
  status: 'draft' | 'saved' | 'submitted';
  createdAt?: Date;
  updatedAt?: Date;
}