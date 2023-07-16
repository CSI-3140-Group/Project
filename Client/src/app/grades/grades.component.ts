import { Component, OnInit, AfterViewInit, ViewChild } from '@angular/core';
import {MatSort, Sort} from '@angular/material/sort';
import {MatTableDataSource} from '@angular/material/table';
import {WebSocketService, Program, Semester, Course, Evaluation} from '../services/websocket.service';


/*export interface Grades{
    code: string;
    name: string;
    units: number;
    grading: string;
    grade: string;
    points: number;
}
*/
/*const grades: Grades[] = [
  { code: 'ABC123', name: 'Course 1', units: 3, grading: 'D (50%) Passing Grade', grade: 'A+', points: 4.0 },
  { code: 'DEF456', name: 'Course 2', units: 3, grading: 'D (50%) Passing Grade', grade: 'B-', points: 3.0 },
  { code: 'GHI789', name: 'Course 3', units: 3, grading: 'D (50%) Passing Grade', grade: 'C', points: 2.0 },
  { code: 'JKL012', name: 'Course 4', units: 3, grading: 'D (50%) Passing Grade', grade: 'A', points: 4.0 },
  { code: 'MNO345', name: 'Course 5', units: 3, grading: 'D (50%) Passing Grade', grade: 'B+', points: 3.5 },
  { code: 'PQR678', name: 'Course 6', units: 3, grading: 'D (50%) Passing Grade', grade: 'B', points: 3.0 },
  { code: 'STU901', name: 'Course 7', units: 3, grading: 'D (50%) Passing Grade', grade: 'C+', points: 2.5 },
  { code: 'VWX234', name: 'Course 8', units: 3, grading: 'D (50%) Passing Grade', grade: 'D+', points: 1.5 },
  { code: 'YZA567', name: 'Course 9', units: 3, grading: 'D (50%) Passing Grade', grade: 'A-', points: 3.7 },
  { code: 'BCD890', name: 'Course 10', units: 3, grading: 'D (50%) Passing Grade', grade: 'B-', points: 2.7 },
  { code: 'EFG123', name: 'Course 11', units: 3, grading: 'D (50%) Passing Grade', grade: 'C', points: 2.0 },
  { code: 'HIJ456', name: 'Course 12', units: 3, grading: 'D (50%) Passing Grade', grade: 'A', points: 4.0 },
  { code: 'KLM789', name: 'Course 13', units: 3, grading: 'D (50%) Passing Grade', grade: 'B', points: 3.0 },
  { code: 'NOP012', name: 'Course 14', units: 3, grading: 'D (50%) Passing Grade', grade: 'C-', points: 1.7 },
];*/

const grades: Course[] = [];

@Component({
  selector: 'grades',
  templateUrl: './grades.component.html',
  styleUrls: ['./grades.component.css']
})
export class GradesComponent implements AfterViewInit {
  dataSource = new MatTableDataSource(grades);
  columnVisibility: {[key: string]: boolean} = {
    code: true,
    name: true,
    units: true,
    grading: true,
    grade: true,
    points: true
  }
  displayedColumns: string[] = ['code', 'name', 'units', 'grading', 'grade', 'points'];

  selectedCourseCode: string | undefined;

  constructor(private webSocketService: WebSocketService){
    webSocketService.socket$.subscribe({
              next: (data: Evaluation) => {
                if(data.id === 'evaluate_program'){
                  for(let semester of data.program.semesters){
                    for(let course of semester.courses){
                      console.log(course);
                      grades.push(course);
                    }
                  }
                }
              },
              error: err => console.log(err),
              complete: () => console.log('complete')
            });
  }

  @ViewChild(MatSort) sort!: MatSort;

  ngAfterViewInit(){
    this.dataSource.sort = this.sort;

    this.dataSource.filterPredicate = (data: Course, filter: string) => {
        const selectedCourseCode = this.selectedCourseCode?.trim().toLowerCase();
        return !selectedCourseCode || data.code.toLowerCase().includes(selectedCourseCode);
      };

      this.dataSource.sortingDataAccessor = (data: Course, sortHeaderId: string) => {
        if (sortHeaderId === 'grade') {
          // Assign numerical values to grades for sorting
          const gradeValue: {[key: string]: number} = {
            'A+': 1,
            'A': 2,
            'A-': 3,
            'B+': 4,
            'B': 5,
            'B-': 6,
            'C+': 7,
            'C': 8,
            'C-': 9,
            'D+': 10,
            'D': 11,
            'D-': 12,
            'F': 13,
            'P': 14,
            'N/A': 15,
          };
          return gradeValue[data.letter];
        }
        return data[sortHeaderId as keyof Course];
      };
  }

  updateColumnVisibility(columnKey: string): void {
    this.columnVisibility[columnKey] = !this.columnVisibility[columnKey];
    this.selectedCourseCode = undefined;
  }

  filterByCourseCode(): void {
    const filterValue = this.selectedCourseCode?.trim().toLowerCase() || "";
    this.dataSource.filter = filterValue;
  }
}
