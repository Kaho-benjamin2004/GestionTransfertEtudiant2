import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, BehaviorSubject, tap } from 'rxjs';
import {
  LoginRequest,
  RegisterRequest,
  AuthResponse,
  RefreshTokenRequest,
  ChangePasswordRequest,
  MessageResponse,
  UserInfo,
  TokenResponse
} from '../models';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = 'http://localhost:8081/auth';
  private currentUserSubject = new BehaviorSubject<UserInfo | null>(this.getUserFromLocalStorage());
  public currentUser$ = this.currentUserSubject.asObservable();

  constructor(private http: HttpClient) {}

  login(credentials: LoginRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/login`, credentials).pipe(
      tap(response => {
        if (response.success && response.accessToken) {
          this.setTokens(response.accessToken, response.refreshToken);
          if (response.user) {
            this.setCurrentUser(response.user);
          }
        }
      })
    );
  }

  register(userData: RegisterRequest): Observable<MessageResponse> {
    return this.http.post<MessageResponse>(`${this.apiUrl}/register`, userData);
  }

  refreshToken(refreshToken: string): Observable<TokenResponse> {
    const request: RefreshTokenRequest = { refreshToken };
    return this.http.post<TokenResponse>(`${this.apiUrl}/refresh`, request).pipe(
      tap(response => {
        if (response.accessToken) {
          this.setAccessToken(response.accessToken);
        }
      })
    );
  }

  changePassword(request: ChangePasswordRequest): Observable<MessageResponse> {
    return this.http.post<MessageResponse>(`${this.apiUrl}/change-password`, request);
  }

  logout(refreshToken: string): Observable<MessageResponse> {
    return this.http.post<MessageResponse>(`${this.apiUrl}/logout`, {}, {
      params: { refreshToken }
    }).pipe(
      tap(() => {
        this.clearTokens();
        this.clearCurrentUser();
      })
    );
  }

  // Token Management
  setTokens(accessToken: string, refreshToken: string): void {
    localStorage.setItem('accessToken', accessToken);
    localStorage.setItem('refreshToken', refreshToken);
  }

  setAccessToken(accessToken: string): void {
    localStorage.setItem('accessToken', accessToken);
  }

  getAccessToken(): string | null {
    return localStorage.getItem('accessToken');
  }

  getRefreshToken(): string | null {
    return localStorage.getItem('refreshToken');
  }

  clearTokens(): void {
    localStorage.removeItem('accessToken');
    localStorage.removeItem('refreshToken');
  }

  // User Management
  setCurrentUser(user: UserInfo): void {
    localStorage.setItem('currentUser', JSON.stringify(user));
    this.currentUserSubject.next(user);
  }

  getCurrentUser(): UserInfo | null {
    return this.currentUserSubject.value;
  }

  getCurrentUser$(): Observable<UserInfo | null> {
    return this.currentUser$;
  }

  clearCurrentUser(): void {
    localStorage.removeItem('currentUser');
    this.currentUserSubject.next(null);
  }

  private getUserFromLocalStorage(): UserInfo | null {
    const user = localStorage.getItem('currentUser');
    return user ? JSON.parse(user) : null;
  }

  isAuthenticated(): boolean {
    return !!this.getAccessToken();
  }

  hasRole(role: string): boolean {
    const user = this.getCurrentUser();
    return user ? user.roles.includes(role) : false;
  }

  hasAnyRole(roles: string[]): boolean {
    const user = this.getCurrentUser();
    return user ? roles.some(role => user.roles.includes(role)) : false;
  }
}
