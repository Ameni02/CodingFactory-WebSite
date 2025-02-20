
import {AuthenticationRequest} from '../../services/models/authentication-request';
import { Router } from '@angular/router';
import { AuthenticationService } from '../../services/services/authentication.service';
import {Component} from "@angular/core";

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent  {

  authRequest: AuthenticationRequest = { email: '', password: '' };
  errorMsg: Array<string> = [];

  constructor(
    private router:Router,
    private authService:AuthenticationService

  ) {
  }


  login() {
    this.errorMsg = [];
    this.authService.authenticate({
      body: this.authRequest
    }).subscribe({
      next: (res) => {
        this.router.navigate(['books']);
      },
      error: (err) => {
        console.log(err);
        this.errorMsg = err.error.validationErrors ?? [err.error.errorMsg];
      }
    });

  }

  register() {
    this.router.navigate(['register']);
  }
}
