export interface LoginRequest {
  username: string;
  password: string;
}

export interface LoginResponse {
  token: string;
  role: string;
  username: string;
}

export interface RegisterRequest {
  username: string;
  email: string;
  role: 'ADMIN_POSTE' | 'USER_POSTE' | 'DGI_USER';
}