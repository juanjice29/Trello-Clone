import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatDialogModule, MatDialog } from '@angular/material/dialog';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatMenuModule } from '@angular/material/menu';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { ProjectService } from '../../services/project.service';
import { AuthService } from '../../services/auth.service';
import { Project, UserRole } from '../../models';
import { ProjectDialogComponent } from '../dialogs/project-dialog/project-dialog.component';

@Component({
  selector: 'app-project-list',
  standalone: true,  imports: [
    CommonModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatDialogModule,
    MatSnackBarModule,
    MatMenuModule,
    MatProgressSpinnerModule
  ],
  templateUrl: './project-list.component.html',
  styleUrls: ['./project-list.component.scss']
})
export class ProjectListComponent implements OnInit {
  projects: Project[] = [];
  isLoading = false;
  constructor(
    private projectService: ProjectService,
    private authService: AuthService,
    private router: Router,
    private dialog: MatDialog,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.loadProjects();
  }

  loadProjects(): void {
    this.isLoading = true;
    this.projectService.getProjects().subscribe({
      next: (projects) => {
        this.projects = projects;
      },
      error: (error) => {
        this.snackBar.open('Failed to load projects', 'Close', { duration: 3000 });
        console.error('Error loading projects:', error);
      },
      complete: () => {
        this.isLoading = false;
      }
    });
  }

  openProject(project: Project): void {
    this.router.navigate(['/kanban', project.id]);
  }
  createProject(): void {
    const dialogRef = this.dialog.open(ProjectDialogComponent, {
      width: '500px',
      data: { isEdit: false }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.projectService.createProject(result).subscribe({
          next: (project) => {
            this.projects.push(project);
            this.snackBar.open('Project created successfully!', 'Close', { duration: 3000 });
          },
          error: (error) => {
            this.snackBar.open('Failed to create project', 'Close', { duration: 3000 });
            console.error('Error creating project:', error);
          }
        });
      }
    });
  }

  editProject(project: Project): void {
    const dialogRef = this.dialog.open(ProjectDialogComponent, {
      width: '500px',
      data: { project, isEdit: true }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.projectService.updateProject(project.id, result).subscribe({
          next: (updatedProject) => {
            const index = this.projects.findIndex(p => p.id === project.id);
            if (index !== -1) {
              this.projects[index] = updatedProject;
            }
            this.snackBar.open('Project updated successfully!', 'Close', { duration: 3000 });
          },
          error: (error) => {
            this.snackBar.open('Failed to update project', 'Close', { duration: 3000 });
            console.error('Error updating project:', error);
          }
        });
      }
    });
  }

  deleteProject(project: Project): void {
    if (confirm(`Are you sure you want to delete "${project.name}"?`)) {
      this.projectService.deleteProject(project.id).subscribe({
        next: () => {
          this.projects = this.projects.filter(p => p.id !== project.id);
          this.snackBar.open('Project deleted successfully!', 'Close', { duration: 3000 });
        },
        error: (error) => {
          this.snackBar.open('Failed to delete project', 'Close', { duration: 3000 });
          console.error('Error deleting project:', error);
        }
      });
    }
  }

  canManageProjects(): boolean {
    const currentUser = this.authService.getCurrentUser();
    console.log('Current User:', currentUser);
    return currentUser?.role === UserRole.PROJECT_MANAGER || currentUser?.role === UserRole.ADMIN;
  }

  getStatusColor(status: string): string {
    switch (status) {
      case 'ACTIVE': return '#4caf50';
      case 'COMPLETED': return '#2196f3';
      case 'ARCHIVED': return '#9e9e9e';
      default: return '#757575';
    }
  }

  formatDate(date: Date): string {
    return new Date(date).toLocaleDateString();
  }
}
