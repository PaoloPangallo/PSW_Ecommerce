import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AdminUsersService } from '../../../service-admin/admin-user.service';
import { User } from '../../../models/user.model';
import { Page } from '../../../models/page.model';

@Component({
  selector: 'app-admin-users',
  templateUrl: './admin-users.component.html',
  styleUrls: ['./admin-users.component.css'],
  standalone: true,
  imports: [CommonModule, FormsModule]
})
export class AdminUsersComponent implements OnInit {
  users: User[] = [];
  editingUser: User | null = null;

  constructor(private adminUsersService: AdminUsersService) {}

  ngOnInit(): void {
    this.loadUsers();
  }

  loadUsers(): void {
    this.adminUsersService.getUsers().subscribe(
      (data: Page<User>) => {
        console.log('Risposta dal server:', data);
        // Assegna l'array di utenti dalla proprietà content
        this.users = data.content;
      },
      error => console.error('Errore nel recupero degli utenti', error)
    );
  }

  onEdit(user: User): void {
    // Clona l'oggetto per evitare modifiche dirette sulla lista
    this.editingUser = { ...user };
  }

  cancelEdit(): void {
    this.editingUser = null;
  }

  onSubmitUpdate(): void {
    if (this.editingUser) {
      this.adminUsersService.updateUser(this.editingUser.id, this.editingUser)
        .subscribe(
          updatedUser => {
            const index = this.users.findIndex(u => u.id === updatedUser.id);
            if (index !== -1) {
              this.users[index] = updatedUser;
            }
            this.editingUser = null;
          },
          error => console.error("Errore nell'aggiornamento dell'utente", error)
        );
    }
  }

  onDelete(user: User): void {
    if (confirm(`Sei sicuro di voler eliminare l'utente ${user.username}?`)) {
      this.adminUsersService.deleteUser(user.id).subscribe(
        () => {
          this.users = this.users.filter(u => u.id !== user.id);
        },
        error => console.error("Errore nell'eliminazione dell'utente", error)
      );
    }
  }
}
