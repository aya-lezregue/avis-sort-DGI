import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { CabService } from '../../core/services/cab.service';
import { EvenementService } from '../../core/services/evenement.service';
import { Cab } from '../../core/models/cab.model';
import { EvenementType } from '../../core/models/evenement.model';

@Component({
  selector: 'app-poste-cabs',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './poste-cabs.component.html'
})
export class PosteCabsComponent implements OnInit {

  cabs: Cab[] = [];
  filteredCabs: Cab[] = [];
  loading = true;
  searchTerm = '';
  filterStatut = '';

  cabEnCours: Cab | null = null;
  nouveauStatut: EvenementType = 'EN_ATTENTE';
  updating = false;

  constructor(
    private cabService: CabService,
    private evenementService: EvenementService
  ) {}

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

  openEditModal(cab: Cab): void {
    this.cabEnCours = cab;
    this.nouveauStatut = cab.statut as EvenementType;
  }

  closeEditModal(): void {
    this.cabEnCours = null;
  }

  confirmerChangementStatut(): void {
    if (!this.cabEnCours) return;
    this.updating = true;

    this.evenementService.ajouterEvenement(this.cabEnCours.id, this.nouveauStatut).subscribe({
      next: () => {
        this.updating = false;
        this.closeEditModal();
        this.loadCabs();
      },
      error: () => {
        this.updating = false;
        alert('Erreur lors de la mise à jour du statut');
      }
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