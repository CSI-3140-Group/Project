import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import {MatInputModule} from '@angular/material/input';
import {MatButtonModule} from '@angular/material/button';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatToolbarModule} from '@angular/material/toolbar';
import {MatIconModule} from '@angular/material/icon';
import { MatTabsModule } from '@angular/material/tabs';
import { RouterModule, Routes } from '@angular/router';
import {MatTableModule} from '@angular/material/table';
import {Sort, MatSortModule} from '@angular/material/sort';
import {MatSelectModule} from '@angular/material/select';
import {MatDatepickerModule} from '@angular/material/datepicker';
import {MatNativeDateModule} from '@angular/material/core';
import { AppComponent } from './app.component';
import { LoginComponent } from './login/login.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { HomeComponent } from './home/home.component';
import { GradesComponent } from './grades/grades.component';
import { TransactionsComponent } from './transactions/transactions.component';
import { NavbarComponent } from './navbar/navbar.component';
import {MatCheckboxModule} from '@angular/material/checkbox';
import {WebSocketService} from './services/websocket.service';
import { MfaComponent } from './mfa/mfa.component';
import {MatProgressSpinnerModule} from '@angular/material/progress-spinner';
import { LoadingDialogComponent } from './loading-dialog/loading-dialog.component';
import { MatDialogModule } from '@angular/material/dialog';
import {MatProgressBarModule} from '@angular/material/progress-bar';


const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: 'mfa', component: MfaComponent },
  { path: 'grades', component: GradesComponent },
  { path: 'transactions', component: TransactionsComponent },
  { path: 'home', component: HomeComponent },
  { path: 'navbar', component: NavbarComponent},
  { path: '', component: LoginComponent, pathMatch: 'full' },
  { path: '**', redirectTo: 'login'}
];

@NgModule({
  declarations: [
    AppComponent,
    LoginComponent,
    HomeComponent,
    GradesComponent,
    TransactionsComponent,
    NavbarComponent,
    MfaComponent,
    LoadingDialogComponent
  ],
  imports: [
    BrowserModule,
    MatInputModule,
    BrowserAnimationsModule,
    MatButtonModule,
    FormsModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatToolbarModule,
    MatIconModule,
    MatTabsModule,
    RouterModule,
    RouterModule.forRoot(routes),
    MatTableModule,
    MatSortModule,
    MatSelectModule,
    MatCheckboxModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatProgressSpinnerModule,
    MatDialogModule,
    MatProgressBarModule
  ],
  exports: [RouterModule],
  providers: [WebSocketService],
  bootstrap: [AppComponent]
})
export class AppModule { }


