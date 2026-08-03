import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class DepotService {
  private readonly API_URL = 'http://localhost:8080/api/depot';

  constructor(private http: HttpClient) {}

  deposer(file: File): Observable<string> {
    const formData = new FormData();
    formData.append('file', file);
    return this.http.post(`${this.API_URL}/csv`, formData, { responseType: 'text' });
  }

  fichiersEnAttente(): Observable<string[]> {
    return this.http.get<string[]>(`${this.API_URL}/pending`);
  }
}