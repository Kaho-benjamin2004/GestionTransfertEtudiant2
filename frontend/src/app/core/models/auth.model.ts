export interface LoginRequest {
  email: string;
  password: string;
}

export interface RegisterRequest {
  nom: string;
  prenom: string;
  email: string;
  password: string;
  confirmPassword: string;
  role?: string;
}

export interface AuthResponse {
  statusCode: number;
  message: string;
  success: boolean;
  accessToken: string;
  refreshToken: string;
  user?: UserInfo;
}

export interface RefreshTokenRequest {
  refreshToken: string;
}

export interface ChangePasswordRequest {
  currentPassword: string;
  newPassword: string;
  confirmPassword: string;
}

export interface MessageResponse {
  statusCode: number;
  message: string;
  success: boolean;
}

export interface UserInfo {
  id: string;
  email: string;
  nom: string;
  prenom: string;
  roles: string[];
}

export interface TokenResponse {
  accessToken: string;
  refreshToken?: string;
  expiresIn?: number;
}
