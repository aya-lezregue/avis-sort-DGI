import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { DepotService } from '../../core/services/depot.service';
import { CabService } from '../../core/services/cab.service';
import { CsvImportResult } from '../../core/models/cab.model';

@Component({
  selector: 'app-poste-upload',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './poste-upload.component.html'
})
export class PosteUploadComponent implements OnInit {

  fichiersEnAttente: string[] = [];
  loading = true;

  traitementEnCours: string | null = null;
  lastResult: CsvImportResult | null = null;
  lastError: string | null = null;

  constructor(
    private depotService: DepotService,
    private cabService: CabService
  ) {}

  ngOnInit(): void {
    this.loadFichiers();
  }

  loadFichiers(): void {
    this.loading = true;
    this.lastResult = null;
    this.lastError = null;
    this.depotService.fichiersEnAttente().subscribe({
      next: (fichiers) => {
        this.fichiersEnAttente = fichiers;
        this.loading = false;
      },
      error: () => this.loading = false
    });
  }

  traiter(nomFichier: string): void {
    this.traitementEnCours = nomFichier;
    this.lastResult = null;
    this.lastError = null;

    this.cabService.traiterFichier(nomFichier).subscribe({
      next: (result) => {
        this.traitementEnCours = null;
        this.lastResult = result;
        this.loadFichiers();
      },
      error: (err) => {
        this.traitementEnCours = null;
        this.lastError = err.error || 'Erreur lors du traitement du fichier';
      }
    });
  }
}