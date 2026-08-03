import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { CabService } from '../../core/services/cab.service';
import { Cab } from '../../core/models/cab.model';

@Component({
  selector: 'app-admin-cabs',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './admin-cabs.component.html'
})
export class AdminCabsComponent implements OnInit {

  cabs: Cab[] = [];
  filteredCabs: Cab[] = [];
  loading = true;
  searchTerm = '';
  filterStatut = '';

  constructor(private cabService: CabService) {}

  ngOnInit(): void {
    this.loadCabs();
  }

  loadCabs(): void {
    this.loading = true;
    this.cabService.getAll().subscribe({
      next: (cabs) => {
        this.cabs = cabs;
        this.applyFilters();
        this.loading = false;
      },
      error: () => this.loading = false
    });
  }

  applyFilters(): void {
    this.filteredCabs = this.cabs.filter(cab => {
      const matchSearch = !this.searchTerm ||
        cab.numeroCab.toLowerCase().includes(this.searchTerm.toLowerCase()) ||
        cab.nomDestinataire.toLowerCase().includes(this.searchTerm.toLowerCase());
      const matchStatut = !this.filterStatut || cab.statut === this.filterStatut;
      return matchSearch && matchStatut;
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