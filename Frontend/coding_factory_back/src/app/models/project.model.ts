export interface Project {
    id: number; 
    title: string;
    field: string;
    startDate: Date;
    endDate: Date;
    archived: boolean;
    companyAddress: string;
    companyEmail: string;
    companyName: string;
    companyPhone: string;
    descriptionFilePath: string;
    numberOfPositions: number;
    professionalSupervisor: string;
    requiredSkills: string;
  }