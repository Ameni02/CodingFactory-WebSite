import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { FormationService } from 'src/app/services/formation.service';
import { NotificationService } from 'src/app/services/notification.service';

@Component({
  selector: 'app-add-training',
  templateUrl: './add-training.component.html',
  styleUrls: ['./add-training.component.css']
})
export class AddTrainingComponent {
  trainingForm: FormGroup;
  selectedFile!: File;
  popup = {
    visible: false,
    message: '',
    type: 'info' // 'success' | 'error' | 'info'
  };

  constructor(
    private fb: FormBuilder,
    private trainingService: FormationService,
    private notificationService: NotificationService // ✅ Inject service
  ) {
    this.trainingForm = this.fb.group({
      title: ['', Validators.required],
      file: [null, Validators.required]
    });
  }

  onFileChange(event: any): void {
    const file = event.target.files[0];
    if (file && file.type === 'application/pdf') {
      this.selectedFile = file;
      this.trainingForm.get('file')?.setValue(file);
    } else {
      this.showPopup("❌ Invalid file type. Only PDF files are allowed.", "error");
    }
  }

  onSubmit(): void {
    if (this.trainingForm.invalid || !this.selectedFile) {
      const message = !this.trainingForm.value.title
        ? "⚠️ Title is required."
        : !this.selectedFile
        ? "⚠️ Please select a PDF file before submitting."
        : "⚠️ All fields are required.";
      this.showPopup(message, "error");
      return;
    }

    const title = this.trainingForm.value.title;

    this.trainingService.uploadPdf(title, this.selectedFile).subscribe({
      next: () => {
        // ✅ Envoi de l'email après succès
        this.notificationService.sendEmail(
          'admin@example.com', // 📧 adresse à personnaliser
          'New Training Added',
          `The training titled "${title}" has been successfully added.`
        ).subscribe({
          next: () => {
            this.showPopup("✅ Training successfully added. A confirmation email has been sent. Please check your inbox.", "success");
            this.trainingForm.reset();
            this.selectedFile = undefined!;
          },
          error: () => {
            this.showPopup("✅ Training added, but email notification failed to send.", "info");
            this.trainingForm.reset();
            this.selectedFile = undefined!;
          }
        });
      },
      error: () => {
        this.showPopup("❌ Failed to upload training. Please try again later.", "error");
      }
    });
  }

  onDragOver(event: DragEvent): void {
    event.preventDefault();
  }

  onDrop(event: DragEvent): void {
    event.preventDefault();
    const file = event.dataTransfer?.files?.[0];
    if (file && file.type === 'application/pdf') {
      this.selectedFile = file;
      this.trainingForm.get('file')?.setValue(file);
      this.showPopup(`📄 File "${file.name}" added successfully.`, "info");
    } else {
      this.showPopup("❌ Only PDF files are accepted.", "error");
    }
  }

  showPopup(message: string, type: 'success' | 'error' | 'info'): void {
    this.popup.message = message;
    this.popup.type = type;
    this.popup.visible = true;
    setTimeout(() => this.popup.visible = false, 4000);
  }
}