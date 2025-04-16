import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { BackRoutingModule } from './back-routing.module';
import { BackComponent } from './back.component';
import { EventsComponent } from './pages/events/events.component';
import { FeedbacksComponent } from './pages/feedbacks/feedbacks.component';
import { ParticipationsComponent } from './pages/participations/participations.component';
import { ReactiveFormsModule } from "@angular/forms";

import { HeaderComponent } from './components/header/header.component';

import { HttpClientModule } from '@angular/common/http';
import { ToastrModule } from 'ngx-toastr';
import { EventService } from '../services/event.service';
import { FeedbackService } from '../services/feedback.service';
import { ParicipationService } from '../services/paricipation.service';
import { NotificationService } from '../services/notification.service';


@NgModule({
  declarations: [
    BackComponent,
    EventsComponent,
    FeedbacksComponent,
    ParticipationsComponent,
 
    HeaderComponent,
  
  ],

  imports: [
    CommonModule,
    BackRoutingModule,
    ReactiveFormsModule,
    HttpClientModule,
    ToastrModule.forRoot()
  ],
  providers: [
    EventService,
    FeedbackService,
    ParicipationService,
    NotificationService
  ]
})
export class BackModule { }
