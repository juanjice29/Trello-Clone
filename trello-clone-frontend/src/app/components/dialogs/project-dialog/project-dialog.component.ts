import { Component, Inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA, MatDialogModule } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatSelectModule } from '@angular/material/select';
import { Project, ProjectStatus } from '../../../models/project.model';

export interface ProjectDialogData {
  project?: Project;
  isEdit: boolean;
}

@Component({
  selector: 'app-project-dialog',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatDialogModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatSelectModule
  ],
  templateUrl: './project-dialog.component.html',
  styleUrl: './project-dialog.component.scss'
})
export class ProjectDialogComponent implements OnInit {
  projectForm: FormGroup;
  projectStatuses = Object.values(ProjectStatus);

  constructor(
    private fb: FormBuilder,
    public dialogRef: MatDialogRef<ProjectDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: ProjectDialogData
  ) {
    this.projectForm = this.fb.group({
      name: ['', [Validators.required, Validators.minLength(3)]],
      description: [''],
      status: [ProjectStatus.ACTIVE, Validators.required]
    });
  }

  ngOnInit(): void {
    if (this.data.isEdit && this.data.project) {
      this.projectForm.patchValue({
        name: this.data.project.name,
        description: this.data.project.description,
        status: this.data.project.status
      });
    }
  }

  onSubmit(): void {
    if (this.projectForm.valid) {
      const formValue = this.projectForm.value;
      const projectData = {
        name: formValue.name,
        description: formValue.description,
        status: formValue.status
      };

      if (this.data.isEdit && this.data.project) {
        this.dialogRef.close({ ...this.data.project, ...projectData });
      } else {
        this.dialogRef.close(projectData);
      }
    }
  }

  onCancel(): void {
    this.dialogRef.close();
  }
}
