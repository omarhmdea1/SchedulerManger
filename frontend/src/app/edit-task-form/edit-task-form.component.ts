import { TaskService } from '../services/task.service';
import { Component, Inject } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import {
  MAT_DIALOG_DATA,
  MatDialogModule,
  MatDialogRef,
} from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { BrowserModule } from '@angular/platform-browser';
import { ErrorHandlingService } from '../services/errorHandling.service';

@Component({
  selector: 'app-edit-task-form',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatDialogModule,
  ],
  templateUrl: './edit-task-form.component.html',
  styleUrl: './edit-task-form.component.css',
})
export class EditTaskFormComponent {
  taskForm: FormGroup;

  constructor(
    private fb: FormBuilder,
    public dialogRef: MatDialogRef<EditTaskFormComponent>,
    private taskService: TaskService,
    private errorHandlingService: ErrorHandlingService,
    @Inject(MAT_DIALOG_DATA) public data: any
  ) {
    this.taskForm = this.fb.group({
      name: [data.name],
      startTime: [data.startTime],
      endTime: [data.endTime],
      duration: [data.duration],
    });
  }

  onSave(): void {
    if (this.taskForm.valid) {
      this.dialogRef.close({ ...this.taskForm.value, id: this.data.id });
    }
  }

  onCancel(): void {
    this.dialogRef.close();
  }

  onDelete() {
    if (this.taskForm.valid) {
      this.taskService.deleteTask(this.data.id).subscribe({
        next: () => {
          this.dialogRef.close({ deleted: true });
        },
        error: (error) => this.errorHandlingService.handleError(error),
      });
    }
  }
}
