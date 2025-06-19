import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Project, ProjectRequest } from '../models';
import { AuthService } from './auth.service';

@Injectable({
  providedIn: 'root'
})
export class ProjectService {
  private readonly API_URL = 'http://localhost:8089/api/projects';

  constructor(
    private http: HttpClient,
    private authService: AuthService
  ) { }

  getProjects(): Observable<Project[]> {
    const headers = this.authService.getAuthHeaders();
    return this.http.get<Project[]>(this.API_URL, { headers });
  }

  getProject(id: number): Observable<Project> {
    const headers = this.authService.getAuthHeaders();
    return this.http.get<Project>(`${this.API_URL}/${id}`, { headers });
  }

  createProject(project: ProjectRequest): Observable<Project> {
    const headers = this.authService.getAuthHeaders();
    return this.http.post<Project>(this.API_URL, project, { headers });
  }

  updateProject(id: number, project: ProjectRequest): Observable<Project> {
    const headers = this.authService.getAuthHeaders();
    return this.http.put<Project>(`${this.API_URL}/${id}`, project, { headers });
  }

  deleteProject(id: number): Observable<any> {
    const headers = this.authService.getAuthHeaders();
    return this.http.delete(`${this.API_URL}/${id}`, { headers });
  }

  addMember(projectId: number, userId: number): Observable<any> {
    const headers = this.authService.getAuthHeaders();
    return this.http.post(`${this.API_URL}/${projectId}/members/${userId}`, {}, { headers });
  }

  removeMember(projectId: number, userId: number): Observable<any> {
    const headers = this.authService.getAuthHeaders();
    return this.http.delete(`${this.API_URL}/${projectId}/members/${userId}`, { headers });
  }
}
