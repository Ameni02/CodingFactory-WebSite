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
  submissionDate?: Date;
  cvAnalysisScore?: number;
  cvAnalysisFeedback?: string;
  detailedScores?: {
    education: number;
    experience: number;
    skills: number;
    projectRelevance: number;
    fieldMatch: number;
    titleMatch: number;
  };

  // For backward compatibility
  cvAnalysisResult?: {
    score: number;
    feedback: string;
    detailedScores: {
      education: number;
      experience: number;
      skills: number;
      projectRelevance: number;
      fieldMatch: number;
      titleMatch: number;
    };
    applicationStatus?: string;
  };
}
