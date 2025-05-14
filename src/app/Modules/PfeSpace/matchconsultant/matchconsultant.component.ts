import { HttpClient } from '@angular/common/http';
import { Component } from '@angular/core';
import { NgForm } from '@angular/forms';
import Swal from 'sweetalert2'; // ✅ SweetAlert2 import

@Component({
  selector: 'app-matchconsultant',
  templateUrl: './matchconsultant.component.html',
  styleUrls: ['./matchconsultant.component.css']
})
export class MatchconsultantComponent {
  problemText: string = '';
  matches: any[] = [];
  loading = false;
  selectedConsultant: any = null;
  errorMessage = '';
  newClient = {
    fullName: '',
    email: ''
  };
  
  isRegisteringClient = false;
  registeredClientId: number | null = null;
  selectedDate: string = ''; // ISO date format (yyyy-mm-dd)
  availableSlots: any[] = [];
  selectedSlot: any = null;

  constructor(private http: HttpClient) {}

  openModal(consultant: any) {
    this.selectedConsultant = consultant;
    this.selectedDate = ''; // reset
    this.availableSlots = [];
    this.selectedSlot = null;
    this.errorMessage = ''; // Reset any previous error
    this.isRegisteringClient = false;
    this.registeredClientId = null;
    this.newClient = { fullName: '', email: '' };
  }

  closeModal() {
    this.selectedConsultant = null;
  }

  findMatch() {
    this.loading = true;
    this.errorMessage = '';
    this.http.post('http://localhost:8080/pfespace/api/clients/match-consultants', {
      problemDescription: this.problemText
    }).subscribe({
      next: (data: any) => {
        this.matches = data;
        this.loading = false;
      },
      error: err => {
        this.loading = false;
        Swal.fire('Error', 'Failed to match consultants. Please try again later.', 'error');
      }
    });
  }

  loadTimeSlots() {
    if (!this.selectedDate || !this.selectedConsultant?.id) return;
    this.http.get<any[]>(`http://localhost:8080/pfespace/api/consultants/${this.selectedConsultant.id}/timeslots?date=${this.selectedDate}`)
      .subscribe({
        next: (data) => {
          this.availableSlots = data.filter(slot => slot.available);
        },
        error: () => {
          Swal.fire('Error', 'Failed to load time slots.', 'error');
        }
      });
  }

  bookConsultation() {
    if (!this.selectedSlot) return;
    if (!this.registeredClientId) {
      // Client not yet registered, show the form
      this.isRegisteringClient = true;
      return;
    }
  
    const payload = {
      clientId: this.registeredClientId,
      consultantId: this.selectedConsultant.id,
      startTime: this.selectedSlot.startTime,
      endTime: this.selectedSlot.endTime
    };

    this.http.post('http://localhost:8080/pfespace/api/clients/consultations/book', payload)
      .subscribe({
        next: () => {
          // ✅ Custom SweetAlert after successful booking
          Swal.fire({
            title: 'Consultation Booked!',
            html: `Your consultation with <strong>${this.selectedConsultant.fullName}</strong> 
                   (<em>${this.selectedConsultant.specialty}</em>) at 
                   <strong>${this.formatSlot(this.selectedSlot)}</strong> has been successfully booked.<br>Please check your email for more details.`,
            icon: 'success',
            confirmButtonText: 'OK'
          }).then(() => {
            this.closeModal(); // Close modal after confirmation
          });
        },
        error: () => {
          Swal.fire('Error', 'Failed to book consultation.', 'error');
        }
      });
  }

  registerNewClient(form: NgForm) {
    if (form.invalid) return;
  
    this.http.post<any>('http://localhost:8080/pfespace/api/clients', this.newClient)
      .subscribe({
        next: (res) => {
          this.registeredClientId = res.id;
          this.isRegisteringClient = false;
          Swal.fire('Client Registered', 'Client registered successfully! Now booking consultation...', 'success');
          this.bookConsultation(); // Retry booking now that client is created
        },
        error: () => {
          Swal.fire('Error', 'Failed to register client.', 'error');
        }
      });
  }

  private formatSlot(slot: any): string {
    const start = new Date(slot.startTime);
    const end = new Date(slot.endTime);
    return `${start.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })} - ${end.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}`;
  }
}
