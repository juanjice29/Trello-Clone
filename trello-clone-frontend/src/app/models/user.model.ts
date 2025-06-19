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
  USER = 'USER',
  PROJECT_MANAGER = 'PROJECT_MANAGER',
  ADMIN = 'ADMIN'
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
  roles: string[];
}

export interface TokenRefreshRequest {
  refreshToken: string;
}

export interface TokenRefreshResponse {
  accessToken: string;
  refreshToken: string;
}
