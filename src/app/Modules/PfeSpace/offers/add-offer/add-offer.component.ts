import { Component } from '@angular/core';
import { NgForm } from '@angular/forms';
import { Router } from '@angular/router';
import { Project } from '../../models/project.model';
import { ProjectService } from '../../services/project.service';

@Component({
  selector: 'app-add-offer',
  templateUrl: './add-offer.component.html',
  styleUrls: ['./add-offer.component.css'],
})
export class AddOfferComponent {
  offer = {
    title: '',
    field: '',
    startDate: '',
    endDate: '',
    companyAddress: '',
    companyEmail: '',
    companyName: '',
    companyPhone: '',
    descriptionFile: null,
    numberOfPositions: 1,
    professionalSupervisor: '',
    requiredSkills: '',
  };

  selectedFile: File | null = null;

  constructor(
    private projectservice: ProjectService, // Inject the OfferService
    private router: Router // Inject the Router for navigation
  ) {}

  // Method to handle the form submission
  onSave(offerForm: NgForm) {
    if (offerForm.invalid) {
      alert('Please fill out all required fields.');
      return; // Prevent submission if form is invalid
    }

    // Create offer data object from the form values
    const offerData = { ...offerForm.value, archived: false };
    console.log('Offer Data:', offerData); // Debugging log

    if (this.selectedFile) {
      console.log('Selected File:', this.selectedFile); // Log the file

      // Call the addOffer method with the offer data and file
      this.projectservice.addProject(offerData, this.selectedFile).subscribe({
        next: (savedOffer: any) => {
          console.log('Offer created successfully:', savedOffer); // Log the response
          alert('Offer created successfully!');
          this.router.navigate(['/offers']); // Navigate to the offers list
        },
        error: (error: any) => {
          console.error('Error creating offer:', error); // Log the error
          alert('Error creating offer. Please try again.');
        }
      });
    } else {
      alert('Please select a file before submitting.'); // Handle case where no file is selected
    }
  }

  // Method to handle file selection
  onFileSelected(event: any) {
    const file = event.target.files[0];
    if (file) {
      this.selectedFile = file;
    }
  }
}
