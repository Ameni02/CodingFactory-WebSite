export interface Application {
  id?: number;
  project: {
    id: number;
    title?: string;
    description?: string;
    status?: string;
  };
  studentName: string;
  studentEmail: string;
  cvFilePath: string;
  coverLetterFilePath: string;
  status: 'PENDING' | 'ACCEPTED' | 'REJECTED';
  archived: boolean;
}