import { Component } from '@angular/core';
import {WebSocketService, Login} from '../services/websocket.service';
import { Router, ActivatedRoute } from '@angular/router';



@Component({
  selector: 'mfa',
  templateUrl: './mfa.component.html',
  styleUrls: ['./mfa.component.css']
})
export class MfaComponent {

    code: number = 0;

   constructor(private webSocketService: WebSocketService, private router: Router, private route: ActivatedRoute){
    this.route.queryParams.subscribe(params => {
      this.code = params['code'];
    });
    webSocketService.socket$.subscribe({
            next: (data: Login) => {
              if(data.id === 'complete_login'){
                this.router.navigate(['/home']);
              }
              if(data.id === 'failed_login'){
                this.router.navigate(['/login']);
              }
            },
            error: err => console.log(err),
            complete: () => console.log('complete')
          });
   }

}
