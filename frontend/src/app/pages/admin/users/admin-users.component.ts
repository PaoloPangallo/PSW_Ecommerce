import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AdminUsersService } from '../../../service-admin/admin-user.service';
import { User } from '../../../models/user.model';
import { Page } from '../../../models/page.model';
import { Router } from '@angular/router';



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

  currentPage = 0;
  pageSize = 5;
  totalPages = 0;

  constructor(private adminUsersService: AdminUsersService, private router: Router) {}

  ngOnInit(): void {
    this.loadUsers();
  }

  loadUsers(): void {
    this.adminUsersService.getUsers(this.currentPage, this.pageSize).subscribe(
      (data: Page<User>) => {
        this.users = data.content;
        this.totalPages = data.totalPages;
        console.log('Risposta dal server:', data);
      },
      error => console.error('Errore nel recupero degli utenti', error)
    );
  }

  nextPage(): void {
    if (this.currentPage < this.totalPages - 1) {
      this.currentPage++;
      this.loadUsers();
    }
  }

  previousPage(): void {
    if (this.currentPage > 0) {
      this.currentPage--;
      this.loadUsers();
    }
  }

  onChangePageSize(size: number): void {
    this.pageSize = size;
    this.currentPage = 0;
    this.loadUsers();
  }

  onEdit(user: User): void {
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

  goBack(): void {
    this.router.navigate(['/admin/dashboard']);
  }


  onDelete(user: User): void {
    if (confirm(`Sei sicuro di voler eliminare l'utente ${user.username}?`)) {
      this.adminUsersService.deleteUser(user.id).subscribe(
        () => {
          this.loadUsers(); // Ricarica la pagina aggiornata
        },
        error => console.error("Errore nell'eliminazione dell'utente", error)
      );
    }
  }
}
