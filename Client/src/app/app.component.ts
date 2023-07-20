import { Component } from '@angular/core';
import { Router, NavigationEnd, Event } from '@angular/router';
import { filter } from 'rxjs/operators';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  hideNavBar: boolean = false;
  constructor(private router: Router) {
      this.router.events.subscribe((event: Event) => {

              if (event instanceof NavigationEnd) {
                  // Hide progress spinner or progress bar
                  if(event.url === '/' || event.url.includes('/mfa') || event.url === '/login'){
                    this.hideNavBar = true;
                  }
                  else{
                  this.hideNavBar = false;
                  }
              }
          });
    }
}
