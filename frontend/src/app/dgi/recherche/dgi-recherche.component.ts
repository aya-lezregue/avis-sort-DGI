import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { CabService } from '../../core/services/cab.service';
import { CabDetailResponse } from '../../core/models/evenement.model';

@Component({
  selector: 'app-dgi-recherche',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './dgi-recherche.component.html'
})
export class DgiRechercheComponent {

  numeroCab = '';
  searching = false;
  notFound = false;
  detail: CabDetailResponse | null = null;

  constructor(private cabService: CabService) {}

  rechercher(): void {
    if (!this.numeroCab.trim()) return;

    this.searching = true;
    this.notFound = false;
    this.detail = null;

    this.cabService.getDetailByNumeroCab(this.numeroCab.trim()).subscribe({
      next: (detail) => {
        this.searching = false;
        this.detail = detail;
      },
      error: () => {
        this.searching = false;
        this.notFound = true;
      }
    });
  }

  etatClass(): string {
    switch (this.detail?.etatActuel) {
      case 'DISTRIBUE': return 'etat-success';
      case 'RETOURNE': return 'etat-warning';
      case 'ECHEC': return 'etat-danger';
      default: return 'etat-neutral';
    }
  }

  etatIcon(): string {
    switch (this.detail?.etatActuel) {
      case 'DISTRIBUE': return 'bi-check-circle-fill';
      case 'RETOURNE': return 'bi-arrow-return-left';
      case 'ECHEC': return 'bi-x-circle-fill';
      default: return 'bi-hourglass-split';
    }
  }
}