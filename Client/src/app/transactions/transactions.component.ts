import { Component, ViewChild, AfterViewInit } from '@angular/core';
import {MatSort, Sort} from '@angular/material/sort';
import {MatTableDataSource} from '@angular/material/table';
import { MAT_DATE_FORMATS, DateAdapter, MAT_DATE_LOCALE, MatDateFormats } from '@angular/material/core';
import { MomentDateAdapter } from '@angular/material-moment-adapter';
import {WebSocketService, Finances, Wallet, Transaction} from '../services/websocket.service';
import { ActivatedRoute } from '@angular/router';

import * as moment from 'moment';

const MY_DATE_FORMATS: MatDateFormats = {
  parse: {
    dateInput: 'YYYY-MM-DD',
  },
  display: {
    dateInput: 'YYYY-MM-DD',
    monthYearLabel: 'MMM YYYY',
    dateA11yLabel: 'YYYY-MM-DD',
    monthYearA11yLabel: 'MMMM YYYY',
  },
};

const transactions: Transaction[] = [];

@Component({
  selector: 'transactions',
  templateUrl: './transactions.component.html',
  styleUrls: ['./transactions.component.css'],
  providers: [
      // Provide the custom date formats
      {
        provide: MAT_DATE_FORMATS,
        useValue: MY_DATE_FORMATS,
      },
      // Provide the moment date adapter and set the locale
      {
        provide: DateAdapter,
        useClass: MomentDateAdapter,
        deps: [MAT_DATE_LOCALE],
      },
    ],
})
export class TransactionsComponent implements AfterViewInit {
  selectedDate: moment.Moment | undefined;
  selectedDate2: moment.Moment | undefined;
  selectedType: string = "";
  types: string[] = [];
  dataSource = new MatTableDataSource(transactions);
  filteredData: Transaction[] = [];
  finances?: Finances;
  columnVisibility: {[key: string]: boolean} = {
    date: true,
    time: true,
    withdrawals: true,
    deposits: true,
    description: true
  }
  displayedColumns: string[] = ['date', 'time', 'withdrawals', 'deposits', 'description'];

  timeOptions: string[] = [
    '00:00:00', '01:00:00', '02:00:00', '03:00:00', '04:00:00',
    '05:00:00', '06:00:00', '07:00:00', '08:00:00', '09:00:00',
    '10:00:00', '11:00:00', '12:00:00', '13:00:00', '14:00:00',
    '15:00:00', '16:00:00', '17:00:00', '18:00:00', '19:00:00',
    '20:00:00', '21:00:00', '22:00:00', '23:00:00',
  ];


  startTime: string | null = null;
  endTime: string | null = null;

  balance: string = "";


  @ViewChild(MatSort) sort!: MatSort;

  constructor(private webSocketService: WebSocketService, private route: ActivatedRoute){

      this.finances = this.webSocketService.getFinances();

      if(this.finances){
        this.balance = this.finances.wallet.balance;
        for(let transaction of this.finances.wallet.transactions){
          transactions.push(transaction);
          this.types.push(transaction.description);
        }
      }

      this.types = Array.from(new Set(this.types));
  }

  ngAfterViewInit(){
    this.dataSource.sort = this.sort;
    this.dataSource.sortingDataAccessor = (transaction, property) => {
        switch (property) {
          case 'withdrawals':
            return this.withdrawalsAccessor(transaction);
          case 'deposits':
            return this.depositsAccessor(transaction);
          default:
            return transaction[property as keyof Transaction];
        }
      };
  }

  updateColumnVisibility(columnKey: string): void {
    this.columnVisibility[columnKey] = !this.columnVisibility[columnKey];
  }

  filterData(): void {
    let filteredData = transactions;

    const startDate = this.selectedDate ? moment(this.selectedDate).format('YYYY-MM-DD') : '';
    const endDate = this.selectedDate2 ? moment(this.selectedDate2).format('YYYY-MM-DD') : '';
    if((this.selectedDate && this.selectedDate2) && (this.selectedDate <= this.selectedDate2)){
      filteredData = filteredData.filter((transaction) => {
        const transactionDate = moment(transaction.date).format('YYYY-MM-DD');
        return (transactionDate >= startDate) && (transactionDate <= endDate);
      });
    }

    if ((this.startTime && this.endTime) && (this.startTime <= this.endTime)){
      filteredData = filteredData.filter((transaction) => {
        const startTime = new Date(`2000-01-01 ${this.startTime}`);
        const endTime = new Date(`2000-01-01 ${this.endTime}`);
        const transactionTime = new Date(`2000-01-01 ${transaction.time}`);
        return transactionTime >= startTime && transactionTime <= endTime;
      });
    }

    if(this.selectedType){
      filteredData = filteredData.filter((transaction) => {
        return transaction.description === this.selectedType;
      });
    }

    this.filteredData = filteredData;
    this.dataSource.data = filteredData;
  }

withdrawalsAccessor = (transaction: Transaction): number => {
  const value = parseFloat(transaction.withdrawal.replace('$', ''));
  return isNaN(value) ? 0 : value;
};

depositsAccessor = (transaction: Transaction): number => {
  const value = parseFloat(transaction.deposit.replace('$', ''));
  return isNaN(value) ? 0 : value;
};


}
