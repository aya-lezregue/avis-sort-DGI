import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent {

  username = '';
  password = '';
  errorMessage = '';
  loading = false;

  constructor(private authService: AuthService, private router: Router) {}

  onSubmit(): void {
    this.errorMessage = '';
    this.loading = true;

    this.authService.login({ username: this.username, password: this.password }).subscribe({
      next: (response) => {
        this.loading = false;
        this.redirectByRole(response.role);
      },
      error: (err) => {
        this.loading = false;
        this.errorMessage = 'Identifiants incorrects';
      }
    });
  }

  private redirectByRole(role: string): void {
    switch (role) {
      case 'ADMIN_POSTE':
        this.router.navigate(['/admin']);
        break;
      case 'USER_POSTE':
        this.router.navigate(['/poste']);
        break;
      case 'DGI_USER':
        this.router.navigate(['/dgi']);
        break;
      default:
        this.router.navigate(['/login']);
    }
  }
}