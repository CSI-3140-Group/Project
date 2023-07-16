import { Component } from '@angular/core';
import {WebSocketService, Login} from './services/websocket.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  title = 'Student Helper';
  isLoggedIn: boolean = false;
  constructor(private webSocketService: WebSocketService){
    webSocketService.socket$.subscribe({
              next: (data: Login) => {
                if(data.id === 'complete_login'){
                  this.isLoggedIn = true;
                }
              },
              error: err => console.log(err),
              complete: () => console.log('complete')
            });
  }
}
