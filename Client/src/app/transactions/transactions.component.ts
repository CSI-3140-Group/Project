import { Component, ViewChild } from '@angular/core';
import {MatSort, Sort} from '@angular/material/sort';
import {MatTableDataSource} from '@angular/material/table';
import { MAT_DATE_FORMATS, DateAdapter, MAT_DATE_LOCALE, MatDateFormats } from '@angular/material/core';
import { MomentDateAdapter } from '@angular/material-moment-adapter';

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

export interface Transactions {
  date: string;
  time: string;
  withdrawals: string;
  deposits: string;
  description: string;
  [key:string]: string;
}

const transactions: Transactions[] = [
  {
    date: '2023-07-01',
    time: '09:30:15',
    withdrawals: '$50.00',
    deposits: '$0.00',
    description: 'Withdrawal for groceries',
  },
  {
    date: '2023-07-02',
    time: '14:15:10',
    withdrawals: '$0.00',
    deposits: '$100.00',
    description: 'Deposit from employer',
  },
  {
    date: '2023-07-03',
    time: '18:45:55',
    withdrawals: '$20.00',
    deposits: '$0.00',
    description: 'Withdrawal for dining out',
  },
  {
    date: '2023-07-04',
    time: '12:20:30',
    withdrawals: '$0.00',
    deposits: '$75.00',
    description: 'Deposit from freelance work',
  },
  {
    date: '2023-07-05',
    time: '08:10:05',
    withdrawals: '$40.00',
    deposits: '$0.00',
    description: 'Withdrawal for gas',
  },
  {
    date: '2023-07-06',
    time: '16:55:40',
    withdrawals: '$0.00',
    deposits: '$200.00',
    description: 'Deposit from savings',
  },
  {
    date: '2023-07-07',
    time: '10:45:25',
    withdrawals: '$60.00',
    deposits: '$0.00',
    description: 'Withdrawal for shopping',
  },
  {
    date: '2023-07-08',
    time: '13:05:50',
    withdrawals: '$0.00',
    deposits: '$50.00',
    description: 'Deposit from refund',
  },
  {
    date: '2023-07-09',
    time: '11:40:35',
    withdrawals: '$25.00',
    deposits: '$0.00',
    description: 'Withdrawal for movie tickets',
  },
  {
    date: '2023-07-10',
    time: '17:30:20',
    withdrawals: '$0.00',
    deposits: '$150.00',
    description: 'Deposit from side gig',
  },
  {
      date: '2023-07-10',
      time: '17:30:20',
      withdrawals: '$0.00',
      deposits: '$150.00',
      description: 'Deposit from side gig',
    },
    {
        date: '2023-07-10',
        time: '17:30:20',
        withdrawals: '$0.00',
        deposits: '$150.00',
        description: 'Deposit from side gig',
      },
];



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
export class TransactionsComponent {
  selectedDate: moment.Moment | undefined;
  selectedDate2: moment.Moment | undefined;
  dataSource = new MatTableDataSource(transactions);
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


  @ViewChild(MatSort) sort!: MatSort;

  ngAfterViewInit(){
    this.dataSource.sort = this.sort;
    this.dataSource.sortingDataAccessor = (transaction, property) => {
        switch (property) {
          case 'withdrawals':
            return this.withdrawalsAccessor(transaction);
          case 'deposits':
            return this.depositsAccessor(transaction);
          default:
            return transaction[property];
        }
      };
  }

  updateColumnVisibility(columnKey: string): void {
    this.columnVisibility[columnKey] = !this.columnVisibility[columnKey];
  }

applyTimeFilter() {
  const filteredData = transactions.filter((transaction) => {
    if ((this.startTime && this.endTime) && (this.startTime <= this.endTime)) {
      const startTime = new Date(`2000-01-01 ${this.startTime}`);
      const endTime = new Date(`2000-01-01 ${this.endTime}`);
      const transactionTime = new Date(`2000-01-01 ${transaction.time}`);

      return transactionTime >= startTime && transactionTime <= endTime;
    }

    return true; // No filter applied
  });

  this.dataSource.data = filteredData;
 }

applyDateFilter() {
  const startDate = this.selectedDate ? moment(this.selectedDate).format('YYYY-MM-DD') : '';
  const endDate = this.selectedDate2 ? moment(this.selectedDate2).format('YYYY-MM-DD') : '';

  const filteredData = transactions.filter((transaction) => {
    if((this.selectedDate && this.selectedDate2) && (this.selectedDate <= this.selectedDate2)){
      const transactionDate = moment(transaction.date).format('YYYY-MM-DD');

          return (
            (transactionDate >= startDate) &&
            (transactionDate <= endDate)
          );
    }

    return true;

  });

  this.dataSource.data = filteredData;
}

withdrawalsAccessor = (transaction: Transactions): number => {
  const value = parseFloat(transaction.withdrawals.replace('$', ''));
  return isNaN(value) ? 0 : value;
};

depositsAccessor = (transaction: Transactions): number => {
  const value = parseFloat(transaction.deposits.replace('$', ''));
  return isNaN(value) ? 0 : value;
};


}
