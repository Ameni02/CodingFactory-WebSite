import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { ConsultantService } from '../services/consultant.service';
import { ClientService } from '../services/client.service';
@Component({
  selector: 'app-consultationrequest',
  templateUrl: './consultationrequest.component.html',
  styleUrls: ['./consultationrequest.component.css']
})
export class ConsultationrequestComponent implements OnInit{
  clientId!: number;
  specialty: string = '';
  startTime: string = '';
  maxEndTime: string = '';
  endTime: string = '';
  message = '';
  toasts: { message: string, type: string }[] = [];  // Array to hold toast notifications

  constructor(
    private route: ActivatedRoute,
    private consultationService: ClientService
  ) {}

  ngOnInit() {
    this.clientId = +this.route.snapshot.paramMap.get('clientId')!;
  }
  showToast(message: string, type: string) {
    this.toasts.push({ message, type });
    // Automatically remove the toast after 5 seconds
    setTimeout(() => this.toasts.shift(), 5000);
  }
  onSubmit() {
    const start = new Date(this.startTime);
    const end = new Date(this.endTime);
    const durationInMinutes = (end.getTime() - start.getTime()) / (1000 * 60); // convert ms to minutes
  
    if (durationInMinutes > 40) {
      this.showToast('End time must be within 40 minutes of the start time.', 'danger');
      return;
    }
  
    if (end <= start) {
      this.showToast('End time must be after the start time.', 'danger');
      return;
    }
  
    this.consultationService
      .requestConsultation(this.clientId, this.specialty, this.startTime, this.endTime)
      .subscribe({
        next: (res) => {
          this.showToast('Consultation successfully scheduled.', 'success');
        },
        error: (err) => {
          console.error('Consultation request failed:', err);
          this.showToast('Failed to request consultation. Please try again.', 'danger');
        }
      });
  }
  updateEndTimeLimit() {
    if (this.startTime) {
      const start = new Date(this.startTime);
      const maxEnd = new Date(start.getTime() + 40 * 60 * 1000); // 40 minutes later
      this.maxEndTime = this.formatLocalDateTime(maxEnd);
  
      if (this.endTime && new Date(this.endTime) > maxEnd) {
        this.endTime = this.maxEndTime;
      }
    }
  }
  formatLocalDateTime(date: Date): string {
    const pad = (n: number) => n.toString().padStart(2, '0');
  
    const year = date.getFullYear();
    const month = pad(date.getMonth() + 1); // months are zero-based
    const day = pad(date.getDate());
    const hours = pad(date.getHours());
    const minutes = pad(date.getMinutes());
  
    return `${year}-${month}-${day}T${hours}:${minutes}`;
  }
  
}
