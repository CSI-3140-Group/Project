import { Component } from '@angular/core';
import {Validators, FormControl} from '@angular/forms';
import {NgIf} from '@angular/common';


@Component({
  selector: 'login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {
  email = new FormControl('', [Validators.required, Validators.email]);
  password = new FormControl('', [Validators.required]);

  getEmailMessage(){
    if(this.email.hasError('required')){
      return 'You must enter a value';
    }

    return this.email.hasError('email') ? 'Not a valid email' : '';
  }

  getPasswordMessage(){
    if(this.password.hasError('required')){
      return 'You must enter a value';
    }
    return '';
  }

  loginUser(){
    console.log('hi');
  }
}
