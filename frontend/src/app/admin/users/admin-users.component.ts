import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { UserService } from '../../core/services/user.service';
import { UserResponse, Role } from '../../core/models/user.model';

@Component({
  selector: 'app-admin-users',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './admin-users.component.html'
})
export class AdminUsersComponent implements OnInit {

  users: UserResponse[] = [];
  loading = true;

  showCreateModal = false;
  creating = false;
  createError = '';

  newUsername = '';
  newPassword = '';
  newEmail = '';
  newRole: Role = 'USER_POSTE';

  constructor(private userService: UserService) {}

  ngOnInit(): void {
    this.loadUsers();
  }

  loadUsers(): void {
    this.loading = true;
    this.userService.getAll().subscribe({
      next: (users) => {
        this.users = users;
        this.loading = false;
      },
      error: () => this.loading = false
    });
  }

  openCreateModal(): void {
    this.newUsername = '';
    this.newPassword = '';
    this.newEmail = '';
    this.newRole = 'USER_POSTE';
    this.createError = '';
    this.showCreateModal = true;
  }

  closeCreateModal(): void {
    this.showCreateModal = false;
  }

submitCreate(): void {
  this.createError = '';
  this.creating = true;

  this.userService.create({
    username: this.newUsername,
    email: this.newEmail,
    role: this.newRole
  }).subscribe({
    next: (message) => {
      this.creating = false;
      this.closeCreateModal();
      this.loadUsers();
      alert(message); // confirme que l'email a bien ete envoye
    },
    error: (err) => {
      this.creating = false;
      this.createError = err.error || 'Erreur lors de la création';
    }
  });
}

  toggleActif(user: UserResponse): void {
    this.userService.toggleActif(user.id).subscribe({
      next: () => this.loadUsers()
    });
  }

  supprimerUser(user: UserResponse): void {
    if (!confirm(`Supprimer l'utilisateur ${user.username} ?`)) return;
    this.userService.delete(user.id).subscribe({
      next: () => this.loadUsers(),
      error: () => alert('Erreur lors de la suppression')
    });
  }

  roleBadgeClass(role: string): string {
    switch (role) {
      case 'ADMIN_POSTE': return 'bg-primary-subtle text-primary';
      case 'USER_POSTE': return 'bg-info-subtle text-info';
      case 'DGI_USER': return 'bg-warning-subtle text-warning';
      default: return 'bg-secondary-subtle text-secondary';
    }
  }
}