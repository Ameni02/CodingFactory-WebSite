import { Project } from './project.model';

export interface ProjectDTO {
  id: number;
  title: string;
  field: string;
  requiredSkills: string;
  companyName: string;
}

export interface MatchProjectsResponse {
  matchedProjects: ProjectMatch[];
  keySkills: string[];
  cvText: string;
}

export interface ProjectMatch {
  project: Project;
  score: number;
}

export interface CoverLetterRequest {
  projectId: number;
  cvText: string;
  studentName: string;
  studentEmail: string;
}

export interface CoverLetterResponse {
  coverLetter: string;
  project: ProjectDTO;
  generatedBy?: string;
}
