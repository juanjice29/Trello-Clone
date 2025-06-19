import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { BehaviorSubject, Observable, tap } from 'rxjs';
import { User, LoginRequest, SignupRequest, JwtResponse, TokenRefreshRequest, TokenRefreshResponse } from '../models';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private readonly API_URL = 'http://localhost:8089/api/auth';
  private readonly TOKEN_KEY = 'token';
  private readonly REFRESH_TOKEN_KEY = 'refreshToken';

  private currentUserSubject = new BehaviorSubject<User | null>(null);
  public currentUser$ = this.currentUserSubject.asObservable();

  private isAuthenticatedSubject = new BehaviorSubject<boolean>(false);
  public isAuthenticated$ = this.isAuthenticatedSubject.asObservable();

  constructor(private http: HttpClient) {
    this.checkAuthStatus();
  }

  private checkAuthStatus(): void {
    const token = this.getToken();
    if (token) {
      this.validateToken().subscribe({
        next: (user) => {
          this.setCurrentUser(user);
          this.isAuthenticatedSubject.next(true);
        },
        error: () => {
          this.logout();
        }
      });
    }
  }

  login(credentials: LoginRequest): Observable<JwtResponse> {
    return this.http.post<JwtResponse>(`${this.API_URL}/signin`, credentials)
      .pipe(
        tap(response => {
          this.setTokens(response.token, response.refreshToken);
          const user: User = {
            id: response.id,
            username: response.username,
            email: response.email,
            firstName: response.firstName,
            lastName: response.lastName,
            role: response.roles[0] as any,
            enabled: true,
            createdAt: new Date(),
            updatedAt: new Date()
          };
          this.setCurrentUser(user);
          this.isAuthenticatedSubject.next(true);
        })
      );
  }

  signup(userData: SignupRequest): Observable<any> {
    return this.http.post(`${this.API_URL}/signup`, userData);
  }

  logout(): void {
    localStorage.removeItem(this.TOKEN_KEY);
    localStorage.removeItem(this.REFRESH_TOKEN_KEY);
    this.currentUserSubject.next(null);
    this.isAuthenticatedSubject.next(false);
  }

  refreshToken(): Observable<TokenRefreshResponse> {
    const refreshToken = this.getRefreshToken();
    const request: TokenRefreshRequest = { refreshToken: refreshToken! };

    return this.http.post<TokenRefreshResponse>(`${this.API_URL}/refreshtoken`, request)
      .pipe(
        tap(response => {
          this.setTokens(response.accessToken, response.refreshToken);
        })
      );
  }

  validateToken(): Observable<User> {
    const headers = this.getAuthHeaders();
    return this.http.get<User>('http://localhost:8089/api/users/me', { headers });
  }

  getToken(): string | null {
    return localStorage.getItem(this.TOKEN_KEY);
  }

  getRefreshToken(): string | null {
    return localStorage.getItem(this.REFRESH_TOKEN_KEY);
  }

  getAuthHeaders(): HttpHeaders {
    const token = this.getToken();
    return new HttpHeaders({
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    });
  }

  getCurrentUser(): User | null {
    return this.currentUserSubject.value;
  }

  isAuthenticated(): boolean {
    return this.isAuthenticatedSubject.value;
  }

  private setTokens(token: string, refreshToken: string): void {
    localStorage.setItem(this.TOKEN_KEY, token);
    localStorage.setItem(this.REFRESH_TOKEN_KEY, refreshToken);
  }

  private setCurrentUser(user: User): void {
    this.currentUserSubject.next(user);
  }
}
