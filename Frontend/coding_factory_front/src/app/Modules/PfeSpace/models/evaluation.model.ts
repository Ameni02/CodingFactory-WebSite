export interface Evaluation {
  id: number;
  deliverable: {
    id: number;
    title?: string;
  };
  grade: number;
  comment: string;
  archived: boolean;
  evaluationDate?: Date;
}
