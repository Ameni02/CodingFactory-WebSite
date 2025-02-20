import { Component } from '@angular/core';
import { ContainerComponent } from "../../../Components/container/container.component";
import { SidebarComponent } from "../../../Components/sidebar/sidebar.component";

@Component({
  selector: 'app-main-page',
  templateUrl: './main-page.component.html',
  styleUrls: ['./main-page.component.css'],


 
})
export class MainPageComponent {
   // Project Stats
   projectStats = {
    pending: 10,
    inProgress: 5,
    completed: 8,
  };

  // Deliverable Stats
  deliverableStats = {
    submitted: 12,
    evaluated: 7,
    pendingChanges: 3,
  };

  // Recent Submissions Data
  recentSubmissions = [
    { student: 'John Doe', project: 'AI Chatbot', submissionDate: '2024-02-19' },
    { student: 'Jane Smith', project: 'E-commerce Platform', submissionDate: '2024-02-18' },
    { student: 'Alice Johnson', project: 'Blockchain Wallet', submissionDate: '2024-02-17' },
  ];

  // Recent Evaluations Data
  recentEvaluations = [
    { evaluator: 'Dr. Smith', project: 'Machine Learning Model', score: 85 },
    { evaluator: 'Prof. Brown', project: 'IoT Smart Home', score: 90 },
    { evaluator: 'Dr. Lee', project: 'Cybersecurity Framework', score: 78 },
  ];

}
