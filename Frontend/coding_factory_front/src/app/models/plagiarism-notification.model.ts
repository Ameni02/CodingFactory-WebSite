export interface PlagiarismNotification {
    type: 'PLAGIARISM_REPORT_READY';
    deliverableId: number;
    reportUrl: string;
} 