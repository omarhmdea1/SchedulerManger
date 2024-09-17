import { MatFormFieldModule } from '@angular/material/form-field';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatDialogModule } from '@angular/material/dialog';
import {
  DateAdapter,
  MAT_DATE_FORMATS,
  MAT_NATIVE_DATE_FORMATS,
  MatNativeDateModule,
  NativeDateAdapter,
} from '@angular/material/core';
import {
  FormBuilder,
  FormGroup,
  FormsModule,
  ReactiveFormsModule,
} from '@angular/forms';
import { MatInputModule } from '@angular/material/input';
import { Component, Inject } from '@angular/core';
import {
  MatDialogRef,
  MAT_DIALOG_DATA,
  MatDialogActions,
} from '@angular/material/dialog';
import { MatTabContent } from '@angular/material/tabs';

@Component({
  selector: 'add-task-form',
  standalone: true,
  imports: [
    MatTabContent,
    MatDialogActions,
    MatFormFieldModule,
    MatDatepickerModule,
    MatDialogModule,
    FormsModule,
    MatNativeDateModule,
    MatInputModule,
    ReactiveFormsModule,
  ],
  templateUrl: './add-task-form.component.html',
  styleUrl: './add-task-form.component.css',
  providers: [
    { provide: DateAdapter, useClass: NativeDateAdapter },
    { provide: MAT_DATE_FORMATS, useValue: MAT_NATIVE_DATE_FORMATS },
  ],
})
export class AddTaskFormComponent {
  addTaskForm: FormGroup;

  constructor(
    public dialogRef: MatDialogRef<AddTaskFormComponent>,
    private fb: FormBuilder,
    @Inject(MAT_DIALOG_DATA) public data: any
  ) {
    this.addTaskForm = this.fb.group({
      name: '',
      startTime: '',
      endTime: '',
      duration: 0,
      repeatEvery: 0,
    });
  }

  onCancel(): void {
    this.dialogRef.close();
  }

  onSave(): void {
    this.dialogRef.close(this.addTaskForm.value);
  }
}
