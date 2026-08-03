export type Role = 'ADMIN_POSTE' | 'USER_POSTE' | 'DGI_USER';

export interface UserResponse {
  id: number;
  username: string;
  email: string;
  role: Role;
  actif: boolean;
}