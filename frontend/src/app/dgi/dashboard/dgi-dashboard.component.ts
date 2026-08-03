import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { DepotService } from '../../core/services/depot.service';
import { ExportService } from '../../core/services/export.service';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-dgi-dashboard',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './dgi-dashboard.component.html'
})
export class DgiDashboardComponent implements OnInit {

  username: string | null = null;
  today = new Date();

  fichiersEnAttente = 0;
  fichiersDisponibles = 0;
  loading = true;

  constructor(
    private depotService: DepotService,
    private exportService: ExportService,
    private authService: AuthService
  ) {
    this.username = this.authService.getUsername();
  }

  ngOnInit(): void {
    this.depotService.fichiersEnAttente().subscribe({
      next: (fichiers) => this.fichiersEnAttente = fichiers.length,
      error: () => {}
    });

    this.exportService.listFichiers().subscribe({
      next: (fichiers) => {
        this.fichiersDisponibles = fichiers.length;
        this.loading = false;
      },
      error: () => this.loading = false
    });
  }
}