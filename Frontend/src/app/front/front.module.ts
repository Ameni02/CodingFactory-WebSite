import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { FrontRoutingModule } from './front-routing.module';
import { FrontComponent } from './front.component';
import { NavbarComponent } from './navbar/navbar.component';
import { EventsComponent } from './events/events.component';
import { HeroSectionComponent } from './hero-section/hero-section.component';
import { AddEventModalComponent } from './components/add-event-modal/add-event-modal.component';
import { EventCardComponent } from './components/event-card/event-card.component';
import { EventDetailComponent } from './event-detail/event-detail.component';
import {FormsModule} from "@angular/forms";
import { FeedbackComponent } from './components/feedback/feedback.component';
import { ServicesComponent } from './components/services/services.component';
import { AboutUsComponent } from './components/about-us/about-us.component';
import { CoursesComponent } from './components/courses/courses.component';
import { TeamComponent } from './components/team/team.component';
import { PfespaceComponent } from './components/pfespace/pfespace.component';
import { ContactUsComponent } from './components/contact-us/contact-us.component';
import { MainComponent } from './components/main/main.component';
import { FooterComponent } from './components/footer/footer.component';


@NgModule({
  declarations: [
    FrontComponent,
    NavbarComponent,
    EventsComponent,
    HeroSectionComponent,
    AddEventModalComponent,
    EventCardComponent,
    EventDetailComponent,
    FeedbackComponent,
    ServicesComponent,
    AboutUsComponent,
    CoursesComponent,
    TeamComponent,
    PfespaceComponent,
    ContactUsComponent,
    MainComponent,
    FooterComponent
  ],
    imports: [
        CommonModule,
        FrontRoutingModule,
        FormsModule
    ]
})
export class FrontModule { }
