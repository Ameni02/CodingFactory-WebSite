import { Component, ViewChild } from '@angular/core';
import dayGridPlugin from '@fullcalendar/daygrid';
import timeGridPlugin from '@fullcalendar/timegrid';
import interactionPlugin from '@fullcalendar/interaction';
import { CalendarOptions } from '@fullcalendar/core';
import { MatTableDataSource } from '@angular/material/table';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import Swal from 'sweetalert2';
import { Consultation } from 'src/app/models/Consulting';
import { ClientService } from 'src/app/services/client.service';
@Component({
  selector: 'app-all-consultations',
  templateUrl: './all-consultations.component.html',
  styleUrls: ['./all-consultations.component.css']
})
export class AllConsultationsComponent {
  displayedColumns: string[] = ['index', 'client', 'consultant', 'specialty', 'status', 'meetingLink', 'startTime', 'endTime'];
  calendarEvents: any[] = [];
  showCalendarView = false; // Flag to toggle calendar view
  calendarPlugins = [dayGridPlugin, timeGridPlugin, interactionPlugin];
// Your original data source
dataSource: any[] = []; 

// Filtered version
filteredDataSource: any[] = [];
  calendarOptions: CalendarOptions = {
    plugins: [dayGridPlugin, timeGridPlugin, interactionPlugin],
    initialView: 'dayGridMonth',
    events: [], 
    editable: true,
    selectable: true,
    select: this.handleDateSelect.bind(this),
    eventClick: this.handleEventClick.bind(this),
    eventDisplay: 'block',

  };



  constructor(private consultationService: ClientService) {}

  ngOnInit(): void {

    this.loadConsultations();
  }

  loadConsultations(): void {
    this.consultationService.getAllConsultations().subscribe(data => {
    

      // Map consultation data to FullCalendar events
      this.calendarEvents = data.map((c, i) => ({
        title: `${c.client.fullName} with ${c.consultant.fullName}`,
        start: c.slot.startTime,
        end: c.slot.endTime,
        backgroundColor: this.getEventColor(c.status),
        borderColor: this.getEventColor(c.status),
        textColor: '#ffffff',
        extendedProps: {
          index: i + 1,
          status: c.status,
          specialty: c.consultant.specialty,
          meetingLink: c.meetingLink,
          clientFullName: c.client.fullName,
          consultantFullName: c.consultant.fullName
        },
        
      }));

      // Update calendar options with mapped events
      this.calendarOptions = {
        ...this.calendarOptions,
        events: this.calendarEvents,
      };
      this.dataSource = data;
    this.filteredDataSource = this.dataSource; // Initialize filtered data
    });
    
  }
  getEventColor(status: string): string {
    switch (status) {
      case 'CONFIRMED':
        return '#378006'; // Green for confirmed consultations
      case 'PENDING':
        return '#f39c12'; // Yellow for pending consultations
      case 'CANCELLED':
        return '#e74c3c'; // Red for cancelled consultations
      case 'COMPLETED':
        return '#2ecc71'; // Green for completed consultations
      default:
        return '#3498db'; // Blue for other statuses
    }
  }
  applyFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value.toLowerCase();
    this.filteredDataSource = this.dataSource.filter(row =>
      row.client?.fullName.toLowerCase().includes(filterValue) ||
      row.consultant?.fullName.toLowerCase().includes(filterValue) ||
      row.consultant?.specialty.toLowerCase().includes(filterValue) ||
      row.status.toLowerCase().includes(filterValue)
    );
  }
  
  toggleView() {
    this.showCalendarView = !this.showCalendarView;
  }

  handleDateSelect(arg: any): void {
    alert(`Date selected: ${arg.startStr}`);
  }

  handleEventClick(arg: any) {
    const event = arg.event;
  
    const eventDetails = event.extendedProps;
    console.log(eventDetails);
  
    // Show SweetAlert2 modal with consultation details
    Swal.fire({
      title: `Consultation with ${eventDetails.clientFullName}`,
      html: `
        <p><strong>Client:</strong> ${eventDetails.clientFullName}</p>
        <p><strong>Consultant:</strong> ${eventDetails.consultantFullName}</p>
        <p><strong>Specialty:</strong> ${eventDetails.specialty}</p>
        <p><strong>Status:</strong> ${eventDetails.status}</p>
        <p><strong>Start Time:</strong> ${event.start.toLocaleString()}</p>
        <p><strong>End Time:</strong> ${event.end.toLocaleString()}</p>
        <p><strong>Meeting Link:</strong> <a href="${eventDetails.meetingLink}" target="_blank">Join Meeting</a></p>
      `,
      icon: 'info',
      confirmButtonText: 'Close',
      width: '500px',
      padding: '20px',
      customClass: {
        popup: 'swal-popup',
        title: 'swal-title',
        htmlContainer: 'swal-html',
      }
    });
  }
}
