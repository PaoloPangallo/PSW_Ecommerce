import { Component } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../services/auth.services'; // Assicurati che il path sia corretto

@Component({
  selector: 'app-navbar',
  standalone: true,
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css'],
  imports: [RouterModule, CommonModule] // Aggiunto CommonModule per usare *ngIf, etc.
})
export class NavbarComponent {
  searchTerm: string = '';

  constructor(
    private router: Router,
    private authService: AuthService
  ) {}

  onSearch(event: Event): void {
    const input = event.target as HTMLInputElement;
    this.searchTerm = input.value.trim();
    this.navigateToSearch();
  }

  search(): void {
    this.navigateToSearch();
  }

  private navigateToSearch(): void {
    this.router.navigate([''], { queryParams: { search: this.searchTerm } });
  }

  handleAccount(): void {
    const token = this.authService.getToken();
    if (token) {
      this.router.navigate(['/user-profile']);
    } else {
      this.router.navigate(['/login']);
    }
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }

  isLoggedIn(): boolean {
    if (typeof window === 'undefined') {
      return false;
    }
    return !!this.authService.getToken();
  }
}
