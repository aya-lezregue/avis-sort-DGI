import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { UserService } from '../../core/services/user.service';
import { AuthService } from '../../core/services/auth.service';
import { UserResponse } from '../../core/models/user.model';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './profile.component.html'
})
export class ProfileComponent implements OnInit {

  user: UserResponse | null = null;
  loading = true;
  error = false;

  ancienMotDePasse = '';
  nouveauMotDePasse = '';
  confirmMotDePasse = '';
  changingPassword = false;
  passwordError = '';
  passwordSuccess = '';

  constructor(
    private userService: UserService,
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.userService.getCurrentUser().subscribe({
      next: (user) => {
        this.user = user;
        this.loading = false;
      },
      error: () => {
        this.error = true;
        this.loading = false;
      }
    });
  }

  roleLabel(): string {
    switch (this.user?.role) {
      case 'ADMIN_POSTE': return 'Administrateur Poste';
      case 'USER_POSTE': return 'Tech';
      case 'DGI_USER': return 'Agent DGI';
      default: return '';
    }
  }

  submitChangePassword(): void {
    this.passwordError = '';
    this.passwordSuccess = '';

    if (this.nouveauMotDePasse !== this.confirmMotDePasse) {
      this.passwordError = 'Les deux mots de passe ne correspondent pas';
      return;
    }

    if (this.nouveauMotDePasse.length < 6) {
      this.passwordError = 'Le nouveau mot de passe doit contenir au moins 6 caractères';
      return;
    }

    this.changingPassword = true;

    this.userService.changePassword({
      ancienMotDePasse: this.ancienMotDePasse,
      nouveauMotDePasse: this.nouveauMotDePasse
    }).subscribe({
      next: (message) => {
        this.changingPassword = false;
        this.passwordSuccess = message;
        this.ancienMotDePasse = '';
        this.nouveauMotDePasse = '';
        this.confirmMotDePasse = '';
      },
      error: (err) => {
        this.changingPassword = false;
        this.passwordError = err.error || 'Erreur lors du changement de mot de passe';
      }
    });
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}