import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterOutlet, RouterLink, RouterLinkActive, Router } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-main-layout',
  standalone: true,
  imports: [CommonModule, RouterOutlet, RouterLink, RouterLinkActive],
  templateUrl: './main-layout.component.html',
  styleUrl: './main-layout.component.css'
})
export class MainLayoutComponent {

username: string | null = null;
role: string | null = null;

constructor(
  private authService: AuthService,
  private router: Router
) {
  this.username = this.authService.getUsername();
  this.role = this.authService.getRole();
}

  get roleLabel(): string {
    switch (this.role) {
      case 'ADMIN_POSTE': return 'Administrateur Poste';
      case 'USER_POSTE': return 'Agent Poste';
      case 'DGI_USER': return 'Agent DGI';
      default: return '';
    }
  }

  get menuItems(): { label: string; icon: string; route: string }[] {
    switch (this.role) {
      case 'ADMIN_POSTE':
        return [
          { label: 'Tableau de bord', icon: 'bi-speedometer2', route: '/admin' },
          { label: 'Dossiers CAB', icon: 'bi-folder', route: '/admin/cabs' },
          { label: 'Utilisateurs', icon: 'bi-people', route: '/admin/users' }
        ];
      case 'USER_POSTE':
        return [
          { label: 'Tableau de bord', icon: 'bi-speedometer2', route: '/poste' },
          { label: 'Dossiers CAB', icon: 'bi-folder', route: '/poste/cabs' },
          { label: 'Importer un fichier', icon: 'bi-upload', route: '/poste/upload' }
        ];
      case 'DGI_USER':
        return [
          { label: 'Tableau de bord', icon: 'bi-speedometer2', route: '/dgi' },
          { label: 'Déposer un fichier', icon: 'bi-cloud-upload', route: '/dgi/depot' },
          { label: 'Rechercher une attestation', icon: 'bi-search', route: '/dgi/recherche' },
          { label: 'Fichiers reçus (OUT)', icon: 'bi-file-earmark-arrow-down', route: '/dgi/fichiers' }
        ];
      default:
        return [];
    }
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}