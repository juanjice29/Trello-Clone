import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { User } from '../models';
import { AuthService } from './auth.service';

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private readonly API_URL = 'http://localhost:8089/api/users';

  constructor(
    private http: HttpClient,
    private authService: AuthService
  ) { }

  getCurrentUser(): Observable<User> {
    const headers = this.authService.getAuthHeaders();
    return this.http.get<User>(`${this.API_URL}/me`, { headers });
  }

  getAllUsers(): Observable<User[]> {
    const headers = this.authService.getAuthHeaders();
    return this.http.get<User[]>(this.API_URL, { headers });
  }

  getUser(id: number): Observable<User> {
    const headers = this.authService.getAuthHeaders();
    return this.http.get<User>(`${this.API_URL}/${id}`, { headers });
  }

  updateUser(id: number, user: Partial<User>): Observable<User> {
    const headers = this.authService.getAuthHeaders();
    return this.http.put<User>(`${this.API_URL}/${id}`, user, { headers });
  }

  deleteUser(id: number): Observable<any> {
    const headers = this.authService.getAuthHeaders();
    return this.http.delete(`${this.API_URL}/${id}`, { headers });
  }
}
