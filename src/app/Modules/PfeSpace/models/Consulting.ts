export interface Consultant {
    id?: number;
    email: string;
    fullName: string;
    specialty: string;
  }
  
  export interface Client {
    id?: number;
    name: string;
    email: string;
  }
  
  export interface Consultation {
    id?: number;
    clientId: number;
    startTime: string;
    endTime: string;
    specialty?: string;
    status: 'PENDING' | 'CONFIRMED' | 'COMPLETED' | 'CANCELLED';
  }