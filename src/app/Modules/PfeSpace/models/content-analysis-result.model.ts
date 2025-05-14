export interface ContentAnalysisResult {
  plagiarismScore: number;
  plagiarismStatus: string; // 'LOW', 'MEDIUM', 'HIGH'
  aiContentScore: number;
  aiContentStatus: string; // 'LOW', 'MEDIUM', 'HIGH'
  overallStatus: string; // 'PASSED', 'WARNING', 'FAILED'
  feedback: string;
  totalMatchCount?: number;
  matchedSources?: Array<{
    similarity: number;
    snippet: string;
    matchCount?: number;
    matchedPhrases?: string[];
  }>;
  analysisDate?: string;
  error?: string;
}
