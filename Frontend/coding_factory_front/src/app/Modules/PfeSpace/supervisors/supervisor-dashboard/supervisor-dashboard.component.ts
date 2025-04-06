import { Component, OnInit } from '@angular/core';
import { DeliverableService } from 'src/services/deliverable.service'; 
import { EvaluationService } from 'src/services/evaluation.service'; 
import { Deliverable } from 'src/app/models/deliverable.model'; 
import { Evaluation } from 'src/app/models/evaluation.model'; 

@Component({
  selector: 'app-supervisor-dashboard',
  templateUrl: './supervisor-dashboard.component.html',
  styleUrls: ['./supervisor-dashboard.component.css'],
})
export class SupervisorDashboardComponent implements OnInit {
  reportsToEvaluate: Deliverable[] = [];
  evaluatedReports: Evaluation[] = [];
  statistics: { totalEvaluated: number; averageGrade: number } = {
    totalEvaluated: 0,
    averageGrade: 0,
  };

  constructor(
    private deliverableService: DeliverableService,
    private evaluationService: EvaluationService
  ) {}

  ngOnInit(): void {
    this.loadReportsToEvaluate();
    this.loadEvaluatedReports();
  }

  loadReportsToEvaluate(): void {
    this.deliverableService. getAllDeliverables().subscribe({
      next: (data) => {
        this.reportsToEvaluate = data.filter(
          (report) => report.status === 'PENDING'
        );
      },
      error: (error) => console.error('Error loading reports to evaluate:', error),
    });
  }

  loadEvaluatedReports(): void {
    this.evaluationService.getEvaluations().subscribe({
      next: (data) => {
        this.evaluatedReports = data;
        this.calculateStatistics();
      },
      error: (error) => console.error('Error loading evaluated reports:', error),
    });
  }

  calculateStatistics(): void {
    const totalEvaluated = this.evaluatedReports.length;
    const totalGrades = this.evaluatedReports.reduce(
      (sum, evaluation) => sum + evaluation.grade,
      0
    );
    const averageGrade = totalEvaluated > 0 ? totalGrades / totalEvaluated : 0;

    this.statistics = {
      totalEvaluated,
      averageGrade,
    };
  }
}