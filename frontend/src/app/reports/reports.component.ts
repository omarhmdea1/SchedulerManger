import { Component, OnInit } from '@angular/core';
import { MatTableModule } from '@angular/material/table';
import { TaskService } from '../services/task.service';
import { ErrorHandlingService } from '../services/errorHandling.service';

export interface TaskReport {
  time: string;
  message: string;
}

@Component({
  selector: 'app-reports',
  standalone: true,
  imports: [MatTableModule],
  templateUrl: './reports.component.html',
  styleUrl: './reports.component.css',
})
export class ReportsComponent implements OnInit {
  displayedColumns: string[] = ['time', 'message'];
  taskReports: TaskReport[] = [];

  constructor(
    private taskReportService: TaskService,
    private errorHandlingService: ErrorHandlingService
  ) {}

  ngOnInit() {
    this.loadTaskReports();
  }

  loadTaskReports(): void {
    this.taskReportService.getTaskReports().subscribe(
      (data: TaskReport[]) => {
        this.taskReports = data;
        this.taskReports.forEach((t) => {
          t.time = new Date(t.time).toLocaleString();
        });
      },
      (error) => this.errorHandlingService.handleError(error)
    );
  }
}
