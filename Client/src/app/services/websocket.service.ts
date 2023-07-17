import { Injectable } from '@angular/core';
import { webSocket, WebSocketSubject } from 'rxjs/webSocket';
import {LoadingDialogComponent} from '../loading-dialog/loading-dialog.component';
import { MatDialog, MatDialogRef } from '@angular/material/dialog';


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
  evaluation?: Evaluation;
  finances?: Finances;

  constructor(private dialog: MatDialog) {
    // Create the WebSocket connection
    this.socket$ = webSocket('ws://localhost:6969/service/');
    let dialogRef: MatDialogRef<LoadingDialogComponent> | null = null;
        this.socket$.subscribe({
            next: (data: unknown) => {
              if((data as Login).id === 'complete_login'){
                dialogRef = this.dialog.open(LoadingDialogComponent, {
                  disableClose: true,
                  backdropClass: 'loading-backdrop',
                  panelClass: 'loading-dialog',
                });
              }
              if((data as Finances).id === 'evaluate_wallet'){
                this.finances = data as Finances;
              }
              if((data as Evaluation).id === 'evaluate_program'){
                this.evaluation = data as Evaluation;
                if(dialogRef){
                  dialogRef.close();
                }

              }
            },
            error: err => console.log(err),
            complete: () => console.log('complete')
          });
  }

  // Function to send JSON data to the WebSocket server
  send(data: any): void {
    this.socket$.next(data);
  }

  getEvaluation(){
    return JSON.stringify(this.evaluation);
  }

  getFinances(){
    return JSON.stringify(this.finances);
  }

}

