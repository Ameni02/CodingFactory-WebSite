export interface Application {
    id: number;
    projectId: number;
    studentName: string;
    studentEmail: string;
    cvFilePath: string;
    coverLetterFilePath: string;
    status: string; // "PENDING", "ACCEPTED", "REJECTED"
    archived: boolean;
  }