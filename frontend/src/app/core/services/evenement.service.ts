import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { EvenementCab, EvenementType } from '../models/evenement.model';

@Injectable({ providedIn: 'root' })
export class EvenementService {
  private readonly API_URL = 'http://localhost:8080/api/evenements';

  constructor(private http: HttpClient) {}

  ajouterEvenement(cabId: number, evenement: EvenementType): Observable<EvenementCab> {
    return this.http.post<EvenementCab>(`${this.API_URL}/${cabId}?evenement=${evenement}`, {});
  }

  getByCabId(cabId: number): Observable<EvenementCab[]> {
    return this.http.get<EvenementCab[]>(`${this.API_URL}/${cabId}`);
  }
}