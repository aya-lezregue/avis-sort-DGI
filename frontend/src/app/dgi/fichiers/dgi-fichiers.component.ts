import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ExportService } from '../../core/services/export.service';

@Component({
  selector: 'app-dgi-fichiers',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './dgi-fichiers.component.html'
})
export class DgiFichiersComponent implements OnInit {

  fichiers: string[] = [];
  loading = true;
  downloadingFile: string | null = null;

  constructor(private exportService: ExportService) {}

  ngOnInit(): void {
    this.loadFichiers();
  }

  loadFichiers(): void {
    this.loading = true;
    this.exportService.listFichiers().subscribe({
      next: (fichiers) => {
        this.fichiers = fichiers;
        this.loading = false;
      },
      error: () => this.loading = false
    });
  }

  telecharger(filename: string): void {
    this.downloadingFile = filename;
    this.exportService.download(filename).subscribe({
      next: (blob) => {
        const url = window.URL.createObjectURL(blob);
        const link = document.createElement('a');
        link.href = url;
        link.download = filename;
        link.click();
        window.URL.revokeObjectURL(url);
        this.downloadingFile = null;
      },
      error: () => {
        this.downloadingFile = null;
        alert('Erreur lors du téléchargement');
      }
    });
  }
}