import { Injectable } from '@angular/core';
import { webSocket, WebSocketSubject } from 'rxjs/webSocket';

@Injectable({
  providedIn: 'root'
})
export class WebSocketService {
  private socket$: WebSocketSubject<any>;

  constructor() {
    // Create the WebSocket connection
    this.socket$ = webSocket('ws://localhost:6969/service/');

    this.socket$.subscribe({
      next: (data) => this.read(data),
      error: err => console.log(err),
      complete: () => console.log('complete')
    });
  }

  // Function to send JSON data to the WebSocket server
  send(data: any): void {
    this.socket$.next(data);
  }

  read(data: any): void {
    console.log(data);
  }

}

