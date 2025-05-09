import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { DeliverableService } from '../../services/deliverable.service';
import { ContentAnalysisService } from '../../services/content-analysis.service';
import { ProjectService } from '../../services/project.service';
import { AcademicSupervisorService } from '../../services/academicsupervisor.service';
import { Router } from '@angular/router';
import { ContentAnalysisResult } from '../../models/content-analysis-result.model';
import { Project } from '../../models/project.model';
import { AcademicSupervisor } from '../../models/academicsupervisor.model';

@Component({
  selector: 'app-submission-form',
  templateUrl: './submission-form.component.html',
  styleUrls: ['./submission-form.component.css'],
})
export class SubmissionFormComponent implements OnInit {
  submissionForm!: FormGroup;
  descriptionFile: File | null = null; // Declare the description file property
  reportFile: File | null = null; // Declare the report file property
  analysisResult: ContentAnalysisResult | null = null;
  isAnalyzing: boolean = false;
  showAnalysisResults: boolean = false;

  // Lists for project and supervisor data
  projects: Project[] = [];
  academicSupervisors: AcademicSupervisor[] = [];
  isLoadingProjects: boolean = false;
  isLoadingSupervisors: boolean = false;

  // For autocomplete suggestions
  projectSuggestions: Project[] = [];
  supervisorSuggestions: AcademicSupervisor[] = [];

  // Selected entities
  selectedProject: Project | null = null;
  selectedSupervisor: AcademicSupervisor | null = null;

  constructor(
    private fb: FormBuilder,
    private deliverableService: DeliverableService,
    private contentAnalysisService: ContentAnalysisService,
    private projectService: ProjectService,
    private academicSupervisorService: AcademicSupervisorService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.submissionForm = this.fb.group({
      projectId: [null, Validators.required],
      projectName: ['', Validators.required],
      academicSupervisorId: [null, Validators.required],
      academicSupervisorName: ['', Validators.required],
      title: ['', Validators.required],
      descriptionFilePath: [null, Validators.required],
      reportFilePath: [null, Validators.required],
      submissionDate: [
        new Date().toISOString().split('T')[0],
        Validators.required,
      ],
    });

    // Load projects and academic supervisors for dropdowns
    this.loadProjects();
    this.loadAcademicSupervisors();
  }

  // Project name input change handler
  onProjectNameChange(event: Event): void {
    const input = event.target as HTMLInputElement;
    const searchTerm = input.value.toLowerCase();

    if (searchTerm.length < 2) {
      this.projectSuggestions = [];
      return;
    }

    this.projectSuggestions = this.projects.filter(project =>
      project.title.toLowerCase().includes(searchTerm)
    ).slice(0, 5); // Limit to 5 suggestions
  }

  // Supervisor name input change handler
  onSupervisorNameChange(event: Event): void {
    const input = event.target as HTMLInputElement;
    const searchTerm = input.value.toLowerCase();

    if (searchTerm.length < 2) {
      this.supervisorSuggestions = [];
      return;
    }

    this.supervisorSuggestions = this.academicSupervisors.filter(supervisor =>
      supervisor.name.toLowerCase().includes(searchTerm)
    ).slice(0, 5); // Limit to 5 suggestions
  }

  // Select a project from suggestions
  selectProject(project: Project): void {
    this.selectedProject = project;
    this.submissionForm.patchValue({
      projectId: project.id,
      projectName: project.title
    });
    this.projectSuggestions = [];
  }

  // Select a supervisor from suggestions
  selectSupervisor(supervisor: AcademicSupervisor): void {
    this.selectedSupervisor = supervisor;
    this.submissionForm.patchValue({
      academicSupervisorId: supervisor.id,
      academicSupervisorName: supervisor.name
    });
    this.supervisorSuggestions = [];
  }

  // Load projects for dropdown
  loadProjects(): void {
    this.isLoadingProjects = true;
    this.projectService.getProjects().subscribe({
      next: (projects) => {
        this.projects = projects;
        this.isLoadingProjects = false;
      },
      error: (error) => {
        console.error('Error loading projects:', error);
        this.isLoadingProjects = false;
      }
    });
  }

  // Load academic supervisors for dropdown
  loadAcademicSupervisors(): void {
    this.isLoadingSupervisors = true;
    this.academicSupervisorService.getAllSupervisors().subscribe({
      next: (supervisors) => {
        this.academicSupervisors = supervisors;
        this.isLoadingSupervisors = false;
      },
      error: (error) => {
        console.error('Error loading academic supervisors:', error);
        this.isLoadingSupervisors = false;
      }
    });
  }

  // Method to handle the form submission
  onSubmit(): void {
    if (this.submissionForm.invalid) {
      alert('Please fill out all required fields.');
      return; // Prevent submission if form is invalid
    }

    // Validate that we have selected a project and supervisor
    if (!this.selectedProject || !this.selectedSupervisor) {
      if (!this.selectedProject) {
        alert('Please select a valid project from the suggestions');
      } else {
        alert('Please select a valid academic supervisor from the suggestions');
      }
      return;
    }

    // Create FormData object
    const formData = new FormData();

    // Get form control values
    const title = this.submissionForm.get('title')?.value;
    const submissionDate = this.submissionForm.get('submissionDate')?.value || new Date().toISOString().split('T')[0];

    // Append form values to FormData
    formData.append('projectId', this.selectedProject.id.toString());
    formData.append('academicSupervisorId', this.selectedSupervisor.id.toString());
    formData.append('title', title);
    formData.append('submissionDate', submissionDate);

    // Add project and supervisor names for reference
    formData.append('projectName', this.selectedProject.title);
    formData.append('academicSupervisorName', this.selectedSupervisor.name);

    // Only append files if they are selected
    if (this.descriptionFile) {
      console.log('Selected Description File:', this.descriptionFile);
      formData.append('descriptionFile', this.descriptionFile);
    } else {
      alert('Please select a description file before submitting.'); // File is missing
      return;
    }

    if (this.reportFile) {
      console.log('Selected Report File:', this.reportFile);
      formData.append('reportFile', this.reportFile);
    } else {
      alert('Please select a report file before submitting.'); // File is missing
      return;
    }

    // Call the service to add the deliverable
    this.deliverableService.addDeliverable(formData).subscribe({
      next: (response) => {
        console.log('Deliverable added successfully:', response);

        // Check if the response contains analysis results
        if (response && typeof response === 'object') {
          if (response.hasOwnProperty('analysisResults')) {
            // @ts-ignore - We know the structure from the backend
            this.analysisResult = response.analysisResults;
            this.showAnalysisResults = true;

            // Show a different message based on the analysis results
            if (this.analysisResult && this.analysisResult.overallStatus) {
              if (this.analysisResult.overallStatus === 'PASSED') {
                alert('Deliverable created successfully! The report passed the plagiarism and AI content check.');
              } else if (this.analysisResult.overallStatus === 'WARNING') {
                alert('Deliverable created with warnings! Please review the plagiarism and AI content analysis results.');
              } else if (this.analysisResult.overallStatus === 'FAILED') {
                alert('Deliverable created, but the report failed the plagiarism and AI content check. Please review the analysis results.');
              } else {
                alert('Deliverable created successfully!');
              }
            } else {
              alert('Deliverable created successfully!');
            }
          } else {
            alert('Deliverable created successfully!');
          }
        } else {
          alert('Deliverable created successfully!');
        }

        // Navigate to deliverables page after a short delay to allow the user to see the analysis results
        setTimeout(() => {
          this.router.navigate(['/deliverables']);
        }, 5000);
      },
      error: (error) => {
        console.error('Error creating deliverable:', error);
        alert('Error creating deliverable. Please try again.');
      }
    });
  }

  handleDescriptionFileInput(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input?.files) {
      this.descriptionFile = input.files[0]; // Get the first file
      this.submissionForm.get('descriptionFilePath')?.setValue(input.files[0].name); // Set the value in form
    }
  }

  handleReportFileInput(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input?.files) {
      this.reportFile = input.files[0]; // Get the first file
      this.submissionForm.get('reportFilePath')?.setValue(input.files[0].name); // Set the value in form

      // Reset analysis results when a new file is selected
      this.analysisResult = null;
      this.showAnalysisResults = false;
    }
  }

  // Method to analyze the report file for plagiarism and AI content
  analyzeReport(): void {
    if (!this.reportFile) {
      alert('Please select a report file first.');
      return;
    }

    this.isAnalyzing = true;
    this.contentAnalysisService.analyzeReport(this.reportFile).subscribe({
      next: (result) => {
        this.analysisResult = result;
        this.showAnalysisResults = true;
        this.isAnalyzing = false;
      },
      error: (error) => {
        console.error('Error analyzing report:', error);
        this.isAnalyzing = false;
        alert('Error analyzing report. Please try again.');
      }
    });
  }
}