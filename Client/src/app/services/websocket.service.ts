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
  term: string;
  year: string;
}

export interface Semester{
  cgpa: string;
  courses: Course[];
  level: string;
  term: string;
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

export interface Finances{
  id: string;
  wallet: Wallet;
}

export interface Wallet {
  balance: string;
  transactions: Transaction[];
}

export interface Transaction{
  balance: string;
  date: string;
  deposit: string;
  description: string;
  time: string;
  withdrawal: string;
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
      next: (data) => console.log(data),
      error: err => console.log(err),
      complete: () => console.log('complete')
    });
  }

  // Function to send JSON data to the WebSocket server
  send(data: any): void {
    this.socket$.next(data);
  }

}

