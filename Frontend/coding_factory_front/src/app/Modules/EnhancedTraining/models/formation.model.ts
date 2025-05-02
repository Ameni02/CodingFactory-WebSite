import { Comment } from './comment.model';

export interface Formation {
  id: number;
  titre: string;
  pdfFileName: string;
  archived: boolean;
  comments?: Comment[];
  averageSentimentScore?: number;
  positiveCommentRatio?: number;
  totalCommentCount?: number;
}
