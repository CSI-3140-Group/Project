import { Component, EventEmitter, Output, ViewChild, ElementRef, AfterViewInit } from '@angular/core';
import {Validators, FormControl} from '@angular/forms';
import {NgIf} from '@angular/common';
import {WebSocketService, Login} from '../services/websocket.service';
import { Router } from '@angular/router';



@Component({
  selector: 'login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements AfterViewInit {
  email = new FormControl('', [Validators.required, Validators.email]);
  password = new FormControl('', [Validators.required]);
  isLoading: boolean = false;
  message: string = "";

  @ViewChild('username') username!: ElementRef;

  constructor(private webSocketService: WebSocketService, private router: Router) {
    webSocketService.socket$.subscribe({
          next: (data: Login) => {
            if(data.id === 'prompt_mfa'){
            const code = data.code;
              this.router.navigate(['/mfa'], { queryParams: { code } });
            }
            if(data.id === 'complete_login'){
              this.router.navigate(['/home']);
            }
            if(data.id === 'failed_login'){
              this.message = "Username or password is incorrect";
              this.isLoading = false;
            }
          },
          error: err => console.log(err),
          complete: () => console.log('complete')
        });
   }

   ngAfterViewInit() {
     setTimeout(() => {
       this.username.nativeElement.focus();
     });
   }


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
    this.isLoading = true;
  }



}
