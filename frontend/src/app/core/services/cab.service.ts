import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Cab, CsvImportResult } from '../models/cab.model';
import { CabDetailResponse } from '../models/evenement.model';

@Injectable({ providedIn: 'root' })
export class CabService {
  private readonly API_URL = 'http://localhost:8080/api/cabs';

  constructor(private http: HttpClient) {}

  getAll(): Observable<Cab[]> {
    return this.http.get<Cab[]>(this.API_URL);
  }

  getById(id: number): Observable<Cab> {
    return this.http.get<Cab>(`${this.API_URL}/${id}`);
  }

  getByNumeroCab(numeroCab: string): Observable<Cab> {
    return this.http.get<Cab>(`${this.API_URL}/numero/${numeroCab}`);
  }

  getDetailByNumeroCab(numeroCab: string): Observable<CabDetailResponse> {
    return this.http.get<CabDetailResponse>(`${this.API_URL}/numero/${numeroCab}/detail`);
  }

  updateStatut(id: number, statut: string): Observable<Cab> {
    return this.http.put<Cab>(`${this.API_URL}/${id}/statut?statut=${statut}`, {});
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.API_URL}/${id}`);
  }

  uploadCsv(file: File): Observable<CsvImportResult> {
    const formData = new FormData();
    formData.append('file', file);
    return this.http.post<CsvImportResult>(`${this.API_URL}/upload`, formData);
  }

  modifierStatut(numeroCab: string, statut: string) {
  return this.http.put<Cab>(
    `${this.API_URL}/numero/${numeroCab}/statut?statut=${statut}`,
    {}
  );
}

traiterFichier(nomFichier: string): Observable<CsvImportResult> {
  return this.http.post<CsvImportResult>(`${this.API_URL}/traiter/${nomFichier}`, {});
}

downloadAttestationPdf(numeroCab: string): Observable<Blob> {
  return this.http.get(`${this.API_URL}/numero/${numeroCab}/attestation/pdf`, { responseType: 'blob' });
}
}