import {Component, OnInit} from '@angular/core';
import {Router} from "@angular/router";

@Component({
  selector: 'app-front',
  templateUrl: './front.component.html',
  styleUrls: ['./front.component.scss']
})
export class FrontComponent {
  hideComponents = false;
  constructor(private router: Router) {
    this.router.events.subscribe(() => {
      this.hideComponents = this.router.url.startsWith('/eventdetail/');
    });
  }

}
