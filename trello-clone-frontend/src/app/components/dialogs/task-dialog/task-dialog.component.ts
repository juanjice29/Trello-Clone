import { Component, Inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA, MatDialogModule } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatSelectModule } from '@angular/material/select';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';
import { Task, TaskStatus, TaskPriority } from '../../../models/task.model';
import { User } from '../../../models/user.model';
import { Label } from '../../../models/label.model';

export interface TaskDialogData {
  task?: Task;
  isEdit: boolean;
  projectId: number;
  users: User[];
  labels: Label[];
}

@Component({
  selector: 'app-task-dialog',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatDialogModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatSelectModule,
    MatDatepickerModule,
    MatNativeDateModule
  ],
  templateUrl: './task-dialog.component.html',
  styleUrl: './task-dialog.component.scss'
})
export class TaskDialogComponent implements OnInit {
  taskForm: FormGroup;
  taskStatuses = Object.values(TaskStatus);
  taskPriorities = Object.values(TaskPriority);

  constructor(
    private fb: FormBuilder,
    public dialogRef: MatDialogRef<TaskDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: TaskDialogData
  ) {
    this.taskForm = this.fb.group({
      title: ['', [Validators.required, Validators.minLength(3)]],
      description: [''],
      status: [TaskStatus.TODO, Validators.required],
      priority: [TaskPriority.MEDIUM, Validators.required],
      assigneeId: [''],
      dueDate: [''],
      labelIds: [[]]
    });
  }

  ngOnInit(): void {
    if (this.data.isEdit && this.data.task) {
      this.taskForm.patchValue({
        title: this.data.task.title,
        description: this.data.task.description,
        status: this.data.task.status,
        priority: this.data.task.priority,
        assigneeId: this.data.task.assignee?.id || '',
        dueDate: this.data.task.dueDate ? new Date(this.data.task.dueDate) : '',
        labelIds: this.data.task.labels?.map(label => label.id) || []
      });
    }
  }

  onSubmit(): void {
    if (this.taskForm.valid) {
      const formValue = this.taskForm.value;
      const taskData = {
        title: formValue.title,
        description: formValue.description,
        status: formValue.status,
        priority: formValue.priority,
        assigneeId: formValue.assigneeId || null,
        dueDate: formValue.dueDate ? formValue.dueDate.toISOString() : null,
        labelIds: formValue.labelIds || [],
        projectId: this.data.projectId
      };

      if (this.data.isEdit && this.data.task) {
        this.dialogRef.close({ ...this.data.task, ...taskData });
      } else {
        this.dialogRef.close(taskData);
      }
    }
  }

  onCancel(): void {
    this.dialogRef.close();
  }
}
