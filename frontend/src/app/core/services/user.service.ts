import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { UserResponse } from '../models/user.model';
import { RegisterRequest } from '../models/auth.model';

@Injectable({ providedIn: 'root' })
export class UserService {
  private readonly API_URL = 'http://localhost:8080/api/users';

  constructor(private http: HttpClient) {}

  getAll(): Observable<UserResponse[]> {
    return this.http.get<UserResponse[]>(this.API_URL);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.API_URL}/${id}`);
  }

  toggleActif(id: number): Observable<UserResponse> {
    return this.http.put<UserResponse>(`${this.API_URL}/${id}/actif`, {});
  }

  create(request: RegisterRequest): Observable<string> {
    return this.http.post(`http://localhost:8080/api/auth/register`, request, { responseType: 'text' });
  }
}