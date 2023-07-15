import { Component, EventEmitter, Output, ViewChild, ElementRef } from '@angular/core';
import {Validators, FormControl} from '@angular/forms';
import {NgIf} from '@angular/common';
import {WebSocketService} from '../services/websocket.service';
import { Router } from '@angular/router';



@Component({
  selector: 'login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {
  email = new FormControl('', [Validators.required, Validators.email]);
  password = new FormControl('', [Validators.required]);

  @Output() login: EventEmitter<any> = new EventEmitter();

  constructor(private webSocketService: WebSocketService, private router: Router) { }

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
    const jsonData = {id: 'request_login', principal: this.email.value, password: this.password.value};
    this.webSocketService.send(jsonData);
    console.log(jsonData);

  }



}
