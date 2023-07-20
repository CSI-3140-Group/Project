import { Component, AfterViewInit, ViewChild } from '@angular/core';
import {MatSort, Sort} from '@angular/material/sort';
import {MatTableDataSource} from '@angular/material/table';
import {WebSocketService, Program, Semester, Course, Evaluation} from '../services/websocket.service';
import { ActivatedRoute } from '@angular/router';

const grades: Course[] = [];

@Component({
  selector: 'grades',
  templateUrl: './grades.component.html',
  styleUrls: ['./grades.component.css']
})
export class GradesComponent implements AfterViewInit {

  dataSource = new MatTableDataSource(grades);
  filteredData: Course[] = [];
  columnVisibility: {[key: string]: boolean} = {
    code: true,
    name: true,
    units: true,
    grading: true,
    grade: true,
    points: true
  }
  displayCompleted: boolean = false;
  displayedColumns: string[] = ['code', 'name', 'units', 'grading', 'grade', 'points'];

  tgpas: Map<string, string> = new Map<string,string>;
  tgpa?: string = "";

  selectedCourseCode: string = "";
  selectedTerm: string = "";
  selectedYear: string = "";
  evaluation?: Evaluation;
  semesters: Semester[] = [];
  years: string[] = [];
  terms: string[] = [];
  cgpas: string[] = [];
  cgpa: string = "";
  codes: string[] = ['1000', '2000', '3000', '4000', '5000', '6000', '7000', '8000', '9000'];
  @ViewChild(MatSort) sort!: MatSort;

  constructor(private webSocketService: WebSocketService, private route: ActivatedRoute){

      this.evaluation = this.webSocketService.getEvaluation();

      if(this.evaluation){
        for(let semester of this.evaluation.program.semesters){
          this.semesters.push(semester);
          this.years.push(semester.year);
          this.terms.push(semester.term);
          this.cgpas.push(semester.cgpa);
          var key = semester.term + " " + semester.year;
          this.tgpas.set(key,semester.tgpa);
          for(let course of semester.courses){
            course.term = semester.term;
            course.year = semester.year;
            grades.push(course);
          }
        }
      }
     this.cgpa = this.cgpas[0];
     this.terms = Array.from(new Set(this.terms));
     this.years = Array.from(new Set(this.years));

  }
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
  }

    filterData(): void {
      let filteredData = grades;

      if (this.selectedCourseCode) {
        filteredData = filteredData.filter((course) => {
          return course.code[0] === this.selectedCourseCode[0];
        });
      }

      if (this.selectedTerm) {
        filteredData = filteredData.filter((course) => {
          return course.term === this.selectedTerm;
        });
      }

      if(this.selectedYear && this.selectedTerm){
        var key = this.selectedTerm + " " + this.selectedYear;
        this.tgpa = this.tgpas.get(key);
      }

      if (this.selectedYear) {
        filteredData = filteredData.filter((course) => {
          return course.year === this.selectedYear;
        });
      }

      if (this.displayCompleted) {
        filteredData = filteredData.filter((course) => {
          return course.letter !== 'N/A';
        });
      }

      this.filteredData = filteredData;
      this.dataSource.data = filteredData;
    }
}
