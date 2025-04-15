// project.model.ts
export interface Project {
  id: number;
  title: string;
  field: string;
  requiredSkills: string;
  descriptionFilePath: string;
  numberOfPositions: number;
  startDate: string; // Keep as string to match LocalDate from backend
  endDate: string;   // Keep as string to match LocalDate from backend
  companyName: string;
  professionalSupervisor: string;
  companyAddress: string;
  companyEmail: string;
  companyPhone: string;
  archived: boolean;
  status?: string;
  // Remove circular references
  // applications?: any[];
  // deliverables?: any[];
}
