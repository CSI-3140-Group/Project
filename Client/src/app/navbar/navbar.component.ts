import { Component, OnInit } from '@angular/core';
import {WebSocketService, Program, Semester, Course, Evaluation, Finances} from '../services/websocket.service';
import { MatDialog } from '@angular/material/dialog';
import {LoadingDialogComponent} from '../loading-dialog/loading-dialog.component';
import { Router } from '@angular/router';


@Component({
  selector: 'navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css']
})
export class NavbarComponent {
  constructor(private dialog: MatDialog, private webSocketService: WebSocketService, private router: Router){ }

  evaluation?: Evaluation;
  finances?: Finances;

  ngOnInit(){
    const dialogRef = this.dialog.open(LoadingDialogComponent, {
        disableClose: true,
        backdropClass: 'loading-backdrop',
        panelClass: 'loading-dialog',
      });
    this.webSocketService.socket$.subscribe({
        next: (data: unknown) => {
          if((data as Finances).id === 'evaluate_wallet'){
            this.finances = data as Finances;
          }
          if((data as Evaluation).id === 'evaluate_program'){
            this.evaluation = data as Evaluation;
            dialogRef.close();
          }
        },
        error: err => console.log(err),
        complete: () => console.log('complete')
      });
  }

  grades(){
    const grades = this.evaluation;
    this.router.navigate(['/grades'], { queryParams: { grades: JSON.stringify(grades) } });
  }

  transactions(){
    const finances = this.finances;
    this.router.navigate(['/transactions'], { queryParams: { finances: JSON.stringify(finances) } });
  }
}
