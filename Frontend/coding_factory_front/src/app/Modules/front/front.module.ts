import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from "@angular/forms";
import { HttpClientModule } from '@angular/common/http';
import { RouterModule } from '@angular/router';

import { FrontRoutingModule } from './front-routing.module';
import { FrontComponent } from './front.component';
import { EventsComponent } from './events/events.component';
import { HeroSectionComponent } from './hero-section/hero-section.component';
import { AddEventModalComponent } from './components/add-event-modal/add-event-modal.component';
import { EventCardComponent } from './components/event-card/event-card.component';
import { EventDetailComponent } from './event-detail/event-detail.component';
import { FeedbackComponent } from './components/feedback/feedback.component';
import { StarRatingComponent } from './components/star-rating/star-rating.component';





@NgModule({
  declarations: [
    FrontComponent,
    EventsComponent,
    HeroSectionComponent,
    AddEventModalComponent,
    EventCardComponent,
    EventDetailComponent,
    FeedbackComponent,
    StarRatingComponent
  ],
  imports: [
    CommonModule,
    FrontRoutingModule,
    FormsModule,
    ReactiveFormsModule,
    HttpClientModule,
    RouterModule
  ],
  schemas: [CUSTOM_ELEMENTS_SCHEMA],
  exports: [
    FrontComponent,
    EventsComponent,
    EventDetailComponent,
    AddEventModalComponent
  ]
})
export class FrontModule { }
