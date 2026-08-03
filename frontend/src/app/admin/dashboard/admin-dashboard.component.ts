import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { CabService } from '../../core/services/cab.service';
import { UserService } from '../../core/services/user.service';
import { AuthService } from '../../core/services/auth.service';
import { Cab } from '../../core/models/cab.model';

@Component({
  selector: 'app-admin-dashboard',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './admin-dashboard.component.html'
})
export class AdminDashboardComponent implements OnInit {

  username: string | null = null;

  totalCabs = 0;
  totalUsers = 0;
  enAttente = 0;
  distribues = 0;
  retournes = 0;
  echecs = 0;
  tauxDistribution = 0;
  loading = true;
  today = new Date();

  recentCabs: Cab[] = [];

  constructor(
    private cabService: CabService,
    private userService: UserService,
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
        this.retournes = cabs.filter(c => c.statut === 'RETOURNE').length;
        this.echecs = cabs.filter(c => c.statut === 'ECHEC').length;
        this.tauxDistribution = this.totalCabs > 0
          ? Math.round((this.distribues / this.totalCabs) * 100)
          : 0;

        this.recentCabs = [...cabs]
          .sort((a, b) => new Date(b.dateImport).getTime() - new Date(a.dateImport).getTime())
          .slice(0, 5);

        this.loading = false;
      },
      error: () => this.loading = false
    });

    this.userService.getAll().subscribe({
      next: (users) => this.totalUsers = users.length
    });
  }

  statutBadgeClass(statut: string): string {
    switch (statut) {
      case 'DISTRIBUE': return 'bg-success-subtle text-success';
      case 'RETOURNE': return 'bg-warning-subtle text-warning';
      case 'ECHEC': return 'bg-danger-subtle text-danger';
      default: return 'bg-secondary-subtle text-secondary';
    }
  }
}