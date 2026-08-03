import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { DepotService } from '../../core/services/depot.service';

@Component({
  selector: 'app-dgi-depot',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './dgi-depot.component.html'
})
export class DgiDepotComponent implements OnInit {

  selectedFile: File | null = null;
  uploading = false;
  resultMessage = '';
  resultError = '';

  fichiersEnAttente: string[] = [];
  loadingList = true;

  constructor(private depotService: DepotService) {}

  ngOnInit(): void {
    this.loadFichiers();
  }

  loadFichiers(): void {
    this.loadingList = true;
    this.depotService.fichiersEnAttente().subscribe({
      next: (fichiers) => {
        this.fichiersEnAttente = fichiers;
        this.loadingList = false;
      },
      error: () => this.loadingList = false
    });
  }

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      this.selectedFile = input.files[0];
      this.resultMessage = '';
      this.resultError = '';
    }
  }

  deposer(): void {
    if (!this.selectedFile) return;

    this.uploading = true;
    this.resultMessage = '';
    this.resultError = '';

    this.depotService.deposer(this.selectedFile).subscribe({
      next: (message) => {
        this.uploading = false;
        this.resultMessage = message;
        this.selectedFile = null;
        this.loadFichiers();
      },
      error: (err) => {
        this.uploading = false;
        this.resultError = err.error || 'Erreur lors du dépôt du fichier';
      }
    });
  }
}