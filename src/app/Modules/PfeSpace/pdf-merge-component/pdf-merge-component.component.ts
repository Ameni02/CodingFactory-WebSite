import { Component } from '@angular/core';
import { PdfMergeService } from '../services/pdf-merge.service';

@Component({
  selector: 'app-pdf-merge-component',
  templateUrl: './pdf-merge-component.component.html',
  styleUrls: ['./pdf-merge-component.component.css']
})
export class PdfMergeComponentComponent {

  selectedFiles: File[] = [];
  downloading = false;
  errorMessage: string | null = null;

  constructor(private pdfMergeService: PdfMergeService) {}

  onFileSelect(event: any): void {
    this.selectedFiles = event.target.files;
    this.errorMessage = null; // Reset error message
  }

  mergeFiles(): void {
    if (this.selectedFiles.length === 0) {
      this.errorMessage = "Please select at least one file.";
      return;
    }

    const formData = new FormData();
    for (const file of this.selectedFiles) {
      formData.append('files', file, file.name);
    }

    this.downloading = true;
    this.pdfMergeService.mergePdf(formData).subscribe({
      next: (blob: Blob) => {
        this.downloading = false;
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = 'Merged_Deliverable.pdf';
        a.click();
      },
      error: () => {
        this.downloading = false;
        this.errorMessage = "An error occurred while merging the files. Please try again.";
      }
    });
  }
}
