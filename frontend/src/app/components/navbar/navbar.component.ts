import { Component } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { debounceTime, switchMap } from 'rxjs/operators';
import { CommonModule } from '@angular/common';
import { ProductService } from '../../services/product.service';
import { Product } from '../../models/product.model';
import { AuthService } from '../../services/auth.services';
import { NgOptimizedImage } from '@angular/common';

@Component({
  selector: 'app-navbar',
  standalone: true,
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css'],
  imports: [RouterModule, CommonModule, ReactiveFormsModule, NgOptimizedImage]
})
export class NavbarComponent {
  searchControl = new FormControl('');
  searchResults: Product[] = [];

  constructor(
    private router: Router,
    private productService: ProductService,
    protected authService: AuthService
  ) {
    this.searchControl.valueChanges.pipe(
      debounceTime(300),
      switchMap(query => this.productService.searchProducts(query ?? ''))
    ).subscribe(results => {
      this.searchResults = results;
    });
  }

  onSelectProduct(product: Product): void {
    this.router.navigate(['/product', product.id]);
    this.searchResults = [];
    this.searchControl.setValue('');
  }

  handleAccount(): void {
    const token = this.authService.getToken();
    this.router.navigate([token ? '/user-profile' : '/login']);
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }

  isLoggedIn(): boolean {
    return typeof window !== 'undefined' && !!this.authService.getToken();
  }
}
