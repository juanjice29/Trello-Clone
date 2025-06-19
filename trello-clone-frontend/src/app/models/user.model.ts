export interface User {
  id: number;
  username: string;
  email: string;
  firstName: string;
  lastName: string;
  role: UserRole;
  enabled: boolean;
  createdAt: Date;
  updatedAt: Date;
}

export enum UserRole {
  USER = 'ROLE_USER',
  PROJECT_MANAGER = 'ROLE_PROJECT_MANAGER',
  ADMIN = 'ROLE_ADMIN'
}

export interface LoginRequest {
  username: string;
  password: string;
}

export interface SignupRequest {
  username: string;
  email: string;
  firstName: string;
  lastName: string;
  password: string;
}

export interface JwtResponse {
  token: string;
  refreshToken: string;
  type: string;
  id: number;
  username: string;
  email: string;
  firstName: string;
  lastName: string;
  role: string;
}

export interface TokenRefreshRequest {
  refreshToken: string;
}

export interface TokenRefreshResponse {
  accessToken: string;
  refreshToken: string;
}
