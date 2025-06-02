import { Component, OnInit } from '@angular/core';
import { UserService } from '../../services/user.service';
import { User } from '../../models/user.model';

@Component({
  selector: 'app-user-list',
  standalone: true,
  templateUrl: './user-list.component.html',
  styleUrls: ['./user-list.component.css'],
})
export class UserListComponent implements OnInit {
  users: User[] = [];

  currentPage: number = 0;
  pageSize: number = 5;
  totalPages: number = 0;

  constructor(private userService: UserService) {}

  ngOnInit(): void {
    this.loadUsers();
  }

  loadUsers(): void {
    this.userService.getAllUsers(this.currentPage, this.pageSize).subscribe(
      (data) => {
        this.users = data.content;
        this.totalPages = data.totalPages;
        console.log('Utenti caricati:', this.users);
      },
      (error) => {
        console.error('Errore nel caricamento degli utenti:', error);
      }
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
}
