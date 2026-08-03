import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { CabService } from '../../core/services/cab.service';
import { DepotService } from '../../core/services/depot.service';
import { AuthService } from '../../core/services/auth.service';
import { Cab } from '../../core/models/cab.model';

@Component({
  selector: 'app-poste-dashboard',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './poste-dashboard.component.html'
})
export class PosteDashboardComponent implements OnInit {

  username: string | null = null;
  today = new Date();

  totalCabs = 0;
  enAttente = 0;
  distribues = 0;
  fichiersEnAttente = 0;
  loading = true;

  constructor(
    private cabService: CabService,
    private depotService: DepotService,
    private authService: AuthService
  ) {
    this.username = this.authService.getUsername();
  }

  ngOnInit(): void {
    this.cabService.getAll().subscribe({
      next: (cabs: Cab[]) => {
        this.totalCabs = cabs.length;
        this.enAttente = cabs.filter(c => c.statut === 'EN_ATTENTE').length;
        this.distribues = cabs.filter(c => c.statut === 'DISTRIBUE').length;
        this.loading = false;
      },
      error: () => this.loading = false
    });

    this.depotService.fichiersEnAttente().subscribe({
      next: (fichiers) => this.fichiersEnAttente = fichiers.length,
      error: () => {}
    });
  }
}