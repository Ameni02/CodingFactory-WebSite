import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { EducationalResourceService } from 'src/app/services/educational-resource.service';

@Component({
  selector: 'app-add-educational-resource',
  templateUrl: './add-educational-resource.component.html',
  styleUrls: ['./add-educational-resource.component.css']
})

export class AddEducationalResourceComponent {

  ressourceForm: FormGroup;
  selectedFile: File | null = null;

  popup = {
    message: '',
    type: 'info', // 'success' | 'error' | 'info'
    visible: false
  };

  constructor(private fb: FormBuilder, private resourceService: EducationalResourceService) {
    this.ressourceForm = this.fb.group({
      titre: ['', Validators.required],
      description: ['', Validators.required],
      formationId: ['', Validators.required]
    });
  }

  onFileChange(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      this.selectedFile = input.files[0];
    }
  }

  onDrop(event: DragEvent): void {
    event.preventDefault();
    if (event.dataTransfer?.files.length) {
      this.selectedFile = event.dataTransfer.files[0];
    }
  }

  onDragOver(event: DragEvent): void {
    event.preventDefault();
  }

  onSubmit(): void {
    if (this.ressourceForm.valid && this.selectedFile) {
      const formData = new FormData();
      formData.append('file', this.selectedFile);
      formData.append('titre', this.ressourceForm.value.titre);
      formData.append('description', this.ressourceForm.value.description);
      formData.append('formationId', this.ressourceForm.value.formationId);

      this.resourceService.uploadRessource(formData).subscribe({
        next: () => this.showPopup('Resource added successfully!', 'success'),
        error: () => this.showPopup("❌ Failed to upload training. Please try again later.", "error")
      });
    }
  }

  showPopup(message: string, type: string): void {
    this.popup.message = message;
    this.popup.type = type;
    this.popup.visible = true;

    setTimeout(() => {
      this.popup.visible = false;
    }, 4000);
  }
}