import { Component, EventEmitter, Output } from '@angular/core';
import { TaskService } from '../services/task.service';
import { MatTable, MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatDialog } from '@angular/material/dialog';
import { AddTaskFormComponent } from '../add-task-form/add-task-form.component';
import { EditTaskFormComponent } from '../edit-task-form/edit-task-form.component';
import { ErrorHandlingService } from '../services/errorHandling.service';

export interface Task {
  name: string;
  startTime: string;
  endTime: string;
  duration: string;
}

@Component({
  selector: 'task-table',
  standalone: true,
  imports: [MatTableModule, MatButtonModule, MatIconModule, MatTable],
  templateUrl: './task-table.component.html',
  styleUrl: './task-table.component.css',
})
export class TaskTableComponent {
  tasks: Task[] = [];

  displayedColumns: string[] = [
    'name',
    'startDate',
    'endDate',
    'duration',
    'edit',
  ];

  @Output() taskAdded = new EventEmitter<void>();

  constructor(
    private taskService: TaskService,
    private dialog: MatDialog,
    private errorHandlingService: ErrorHandlingService
  ) {}

  ngOnInit(): void {
    this.reloadTasks();
  }

  reloadTasks(): void {
    this.taskService.getTasks().subscribe(
      (data) => {
        this.tasks = data.sort(this.sortByEarliest);
      },
      (error) => this.errorHandlingService.handleError(error)
    );
  }

  sortByEarliest(a: Task, b: Task): number {
    const dateA = new Date(a.startTime);
    const dateB = new Date(b.endTime);

    return dateA.getTime() - dateB.getTime();
  }

  calculateDuration(start: string, end: string): number {
    const startTime: Date = new Date(start);
    const endTime: Date = new Date(end);

    const durationInMilliseconds: number =
      endTime.getTime() - startTime.getTime();
    const durationInMinutes: number = durationInMilliseconds / (1000 * 60);

    return durationInMinutes;
  }

  onEdit(task: Task) {
    const dialogRef = this.dialog.open(EditTaskFormComponent, {
      width: '400px',
      data: task,
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result && !result?.deleted) {
        this.taskService.updateTask(result).subscribe({
          next: () => {
            this.reloadTasks();
          },
          error: (error) => this.errorHandlingService.handleError(error),
        });
      } else if (result?.deleted) {
        this.reloadTasks();
      }
    });
  }

  onSave(): void {
    console.log(this.dialog);
    const dialogRef = this.dialog.open(AddTaskFormComponent, {
      width: '400px',
      data: {},
    });

    dialogRef.afterClosed().subscribe((result) => {
      console.log('The dialog was closed', result);
      if (result) {
        this.taskService.createTask(result).subscribe({
          next: () => {
            this.reloadTasks();
          },
          error: (error) => this.errorHandlingService.handleError(error),
        });
      }
    });
  }
}
