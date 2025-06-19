import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Task, TaskRequest, TaskMoveRequest } from '../models';
import { AuthService } from './auth.service';

@Injectable({
  providedIn: 'root'
})
export class TaskService {
  private readonly API_URL = 'http://localhost:8089/api/projects';

  constructor(
    private http: HttpClient,
    private authService: AuthService
  ) { }

  getProjectTasks(projectId: number): Observable<Task[]> {
    const headers = this.authService.getAuthHeaders();
    return this.http.get<Task[]>(`${this.API_URL}/${projectId}/tasks`, { headers });
  }

  getTask(projectId: number, taskId: number): Observable<Task> {
    const headers = this.authService.getAuthHeaders();
    return this.http.get<Task>(`${this.API_URL}/${projectId}/tasks/${taskId}`, { headers });
  }

  createTask(projectId: number, task: TaskRequest): Observable<Task> {
    const headers = this.authService.getAuthHeaders();
    return this.http.post<Task>(`${this.API_URL}/${projectId}/tasks`, task, { headers });
  }

  updateTask(projectId: number, taskId: number, task: TaskRequest): Observable<Task> {
    const headers = this.authService.getAuthHeaders();
    return this.http.put<Task>(`${this.API_URL}/${projectId}/tasks/${taskId}`, task, { headers });
  }

  deleteTask(projectId: number, taskId: number): Observable<any> {
    const headers = this.authService.getAuthHeaders();
    return this.http.delete(`${this.API_URL}/${projectId}/tasks/${taskId}`, { headers });
  }

  moveTask(projectId: number, taskId: number, moveRequest: TaskMoveRequest): Observable<any> {
    const headers = this.authService.getAuthHeaders();
    const url = `${this.API_URL}/${projectId}/tasks/${taskId}/move?status=${moveRequest.status}&position=${moveRequest.position}`;
    return this.http.put(url, {}, { headers });
  }

  assignTask(projectId: number, taskId: number, userId: number): Observable<any> {
    const headers = this.authService.getAuthHeaders();
    return this.http.put(`${this.API_URL}/${projectId}/tasks/${taskId}/assign/${userId}`, {}, { headers });
  }
}
