import { Injectable } from '@angular/core';
import { webSocket, WebSocketSubject } from 'rxjs/webSocket';

export interface Login{
  id: string;
  code?: number;
}

export interface Course{
  code: string;
  description: string;
  grading: string;
  letter: string;
  points: string;
  program: string;
  units: string;
}

export interface Semester{
  cgpa: string;
  courses: Course[];
  level: string;
  tgpa: string;
  year: string;
}

export interface Program{
  semesters: Semester[];
}

export interface Evaluation{
  id: string;
  program: Program;
}

@Injectable({
  providedIn: 'root'
})
export class WebSocketService {
  public socket$: WebSocketSubject<any>;

  constructor() {
    // Create the WebSocket connection
    this.socket$ = webSocket('ws://localhost:6969/service/');

    this.socket$.subscribe({
      next: (data) => console.log(),
      error: err => console.log(err),
      complete: () => console.log('complete')
    });
  }

  // Function to send JSON data to the WebSocket server
  send(data: any): void {
    this.socket$.next(data);
  }

}

