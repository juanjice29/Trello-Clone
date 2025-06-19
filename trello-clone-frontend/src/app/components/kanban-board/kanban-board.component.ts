import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { CdkDragDrop, DragDropModule, moveItemInArray, transferArrayItem } from '@angular/cdk/drag-drop';
import { TaskService } from '../../services/task.service';
import { ProjectService } from '../../services/project.service';
import { UserService } from '../../services/user.service';
import { Task, TaskStatus, Project, User, Label } from '../../models';
import { TaskCardComponent } from '../task-card/task-card.component';
import { TaskDialogComponent } from '../dialogs/task-dialog/task-dialog.component';

@Component({
  selector: 'app-kanban-board',
  standalone: true,  imports: [
    CommonModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatSnackBarModule,
    MatDialogModule,
    MatProgressSpinnerModule,
    DragDropModule,
    TaskCardComponent
  ],
  templateUrl: './kanban-board.component.html',
  styleUrls: ['./kanban-board.component.scss']
})
export class KanbanBoardComponent implements OnInit {
  project: Project | null = null;
  projectId!: number;

  todoTasks: Task[] = [];
  inProgressTasks: Task[] = [];
  doneTasks: Task[] = [];

  users: User[] = [];
  labels: Label[] = [];

  isLoading = false;
  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private taskService: TaskService,
    private projectService: ProjectService,
    private userService: UserService,
    private dialog: MatDialog,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      this.projectId = +params['id'];      if (this.projectId) {
        this.loadProject();
        this.loadTasks();
        this.loadUsers();
        this.loadLabels();
      }
    });
  }

  loadProject(): void {
    this.projectService.getProject(this.projectId).subscribe({
      next: (project) => {
        this.project = project;
      },
      error: (error) => {
        this.snackBar.open('Failed to load project', 'Close', { duration: 3000 });
        console.error('Error loading project:', error);
      }
    });
  }
  loadTasks(): void {
    this.isLoading = true;
    this.taskService.getProjectTasks(this.projectId).subscribe({
      next: (tasks) => {
        this.organizeTasks(tasks);
      },
      error: (error) => {
        this.snackBar.open('Failed to load tasks', 'Close', { duration: 3000 });
        console.error('Error loading tasks:', error);
      },
      complete: () => {
        this.isLoading = false;
      }
    });
  }
  loadUsers(): void {
    this.userService.getAllUsers().subscribe({
      next: (users: User[]) => {
        this.users = users;
      },
      error: (error: any) => {
        console.error('Error loading users:', error);
      }
    });
  }

  loadLabels(): void {
    // For now, we'll use an empty array. You can implement a label service later
    this.labels = [];
  }

  organizeTasks(tasks: Task[]): void {
    this.todoTasks = tasks.filter(task => task.status === TaskStatus.TODO);
    this.inProgressTasks = tasks.filter(task => task.status === TaskStatus.IN_PROGRESS);
    this.doneTasks = tasks.filter(task => task.status === TaskStatus.DONE);
  }

  onTaskDrop(event: CdkDragDrop<Task[]>): void {
    if (event.previousContainer === event.container) {
      // Moving within the same column
      moveItemInArray(event.container.data, event.previousIndex, event.currentIndex);
    } else {
      // Moving between columns
      const task = event.previousContainer.data[event.previousIndex];
      const newStatus = this.getStatusFromContainer(event.container.id);

      transferArrayItem(
        event.previousContainer.data,
        event.container.data,
        event.previousIndex,
        event.currentIndex
      );

      // Update task status on backend
      this.updateTaskStatus(task, newStatus, event.currentIndex + 1);
    }
  }

  private getStatusFromContainer(containerId: string): TaskStatus {
    switch (containerId) {
      case 'todo-list': return TaskStatus.TODO;
      case 'inprogress-list': return TaskStatus.IN_PROGRESS;
      case 'done-list': return TaskStatus.DONE;
      default: return TaskStatus.TODO;
    }
  }

  private updateTaskStatus(task: Task, newStatus: TaskStatus, newPosition: number): void {
    this.taskService.moveTask(this.projectId, task.id, { status: newStatus, position: newPosition }).subscribe({
      next: () => {
        task.status = newStatus;
        task.position = newPosition;
      },
      error: (error) => {
        this.snackBar.open('Failed to update task', 'Close', { duration: 3000 });
        // Reload tasks to revert UI changes
        this.loadTasks();
        console.error('Error updating task:', error);
      }
    });
  }
  createTask(): void {
    const dialogRef = this.dialog.open(TaskDialogComponent, {
      width: '600px',
      data: {
        isEdit: false,
        projectId: this.projectId,
        users: this.users,
        labels: this.labels
      }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.taskService.createTask(this.projectId, result).subscribe({
          next: (task: Task) => {
            // Add to the appropriate column
            switch (task.status) {
              case TaskStatus.TODO:
                this.todoTasks.push(task);
                break;
              case TaskStatus.IN_PROGRESS:
                this.inProgressTasks.push(task);
                break;
              case TaskStatus.DONE:
                this.doneTasks.push(task);
                break;
            }
            this.snackBar.open('Task created successfully!', 'Close', { duration: 3000 });
          },
          error: (error: any) => {
            this.snackBar.open('Failed to create task', 'Close', { duration: 3000 });
            console.error('Error creating task:', error);
          }
        });
      }
    });
  }

  editTask(task: Task): void {
    const dialogRef = this.dialog.open(TaskDialogComponent, {
      width: '600px',
      data: {
        task,
        isEdit: true,
        projectId: this.projectId,
        users: this.users,
        labels: this.labels
      }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.taskService.updateTask(this.projectId, task.id, result).subscribe({
          next: (updatedTask: Task) => {
            // Update the task in the appropriate array
            this.updateTaskInArray(task, updatedTask);
            this.snackBar.open('Task updated successfully!', 'Close', { duration: 3000 });
          },
          error: (error: any) => {
            this.snackBar.open('Failed to update task', 'Close', { duration: 3000 });
            console.error('Error updating task:', error);
          }
        });
      }
    });
  }

  deleteTask(task: Task): void {
    if (confirm(`Are you sure you want to delete "${task.title}"?`)) {
      this.taskService.deleteTask(this.projectId, task.id).subscribe({
        next: () => {
          this.removeTaskFromArray(task);
          this.snackBar.open('Task deleted successfully!', 'Close', { duration: 3000 });
        },
        error: (error: any) => {
          this.snackBar.open('Failed to delete task', 'Close', { duration: 3000 });
          console.error('Error deleting task:', error);
        }
      });
    }
  }

  private updateTaskInArray(oldTask: Task, newTask: Task): void {
    // Remove from old array
    this.removeTaskFromArray(oldTask);

    // Add to new array based on new status
    switch (newTask.status) {
      case TaskStatus.TODO:
        this.todoTasks.push(newTask);
        break;
      case TaskStatus.IN_PROGRESS:
        this.inProgressTasks.push(newTask);
        break;
      case TaskStatus.DONE:
        this.doneTasks.push(newTask);
        break;
    }
  }

  private removeTaskFromArray(task: Task): void {
    this.todoTasks = this.todoTasks.filter(t => t.id !== task.id);
    this.inProgressTasks = this.inProgressTasks.filter(t => t.id !== task.id);
    this.doneTasks = this.doneTasks.filter(t => t.id !== task.id);
  }

  goBack(): void {
    this.router.navigate(['/dashboard']);
  }
}
