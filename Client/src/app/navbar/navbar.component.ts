import { Component, Output, EventEmitter } from '@angular/core';
import { Router } from '@angular/router';


@Component({
  selector: 'navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css']
})
export class NavbarComponent {

  @Output() logoutEvent: EventEmitter<any> = new EventEmitter();


  logout(){
    this.logoutEvent.emit(true);
  }
}
