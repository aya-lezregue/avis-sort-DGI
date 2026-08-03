import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class ExportService {
  private readonly API_URL = 'http://localhost:8080/api/export';

  constructor(private http: HttpClient) {}

  listFichiers(): Observable<string[]> {
    return this.http.get<string[]>(`${this.API_URL}/list`);
  }

  download(filename: string): Observable<Blob> {
    return this.http.get(`${this.API_URL}/download/${filename}`, { responseType: 'blob' });
  }
}