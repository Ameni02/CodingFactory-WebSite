import {Component, Input} from '@angular/core';
import {ToastrService} from "ngx-toastr";
import { Router} from "@angular/router";

@Component({
  selector: 'app-event-card',
  templateUrl: './event-card.component.html',
  styleUrls: ['./event-card.component.scss']
})
export class EventCardComponent {

  @Input() event: any;
  private _eventCover: string | undefined;
  constructor(private toastr:ToastrService,private router:Router) {
  }
  get eventCover(): string | undefined {
    if(this.event.image)
    {
      return 'data:image/jpg;base64,' + this.event.image;
    }
    return 'assets/images/events/01.jpg';
  }

  goToDetailEvent(event:any) {
    this.toastr.success("Go to detail event");
    this.router.navigate(['/eventdetail', event.id], { state: { eventData: event } });
  }
}
