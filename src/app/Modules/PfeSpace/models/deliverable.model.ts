export interface Deliverable {
  id: number;
  projectId?: number;
  academicSupervisorId: number;
  title: string;
  descriptionFilePath: string;
  reportFilePath: string;
  submissionDate: Date;
  status: string; // "EVALUATED", "PENDING", "REJECTED"
  archived: boolean;
}
