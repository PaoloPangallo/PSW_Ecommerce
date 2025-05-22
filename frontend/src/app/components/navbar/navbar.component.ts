import {Component, ElementRef, HostListener} from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { debounceTime, distinctUntilChanged, switchMap, tap, catchError, of } from 'rxjs';
import { CommonModule } from '@angular/common';
import { ProductService } from '../../services/product.service';
import { Product } from '../../models/product.model';
import { AuthService } from '../../services/auth.services';
import { NgOptimizedImage } from '@angular/common';

@Component({
  selector: 'app-navbar',
  standalone: true,
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.scss'],
  imports: [RouterModule, CommonModule, ReactiveFormsModule, NgOptimizedImage]
})
export class NavbarComponent {
  searchControl = new FormControl('');
  searchResults: Product[] = [];
  isLoading = false;
  isDropdownOpen = false;


  constructor(
    private router: Router,
    private productService: ProductService,
    protected authService: AuthService,
    private eRef: ElementRef // ✅ per rilevare click esterni
  ) {
    this.searchControl.valueChanges.pipe(
      debounceTime(300),
      distinctUntilChanged(),
      tap(() => {
        this.isLoading = true;
        this.isDropdownOpen = true; // mostra suggerimenti
      }),
      switchMap(query =>
        (query && query.length > 1)
          ? this.productService.searchProducts(query).pipe(
            catchError(err => {
              console.error('Errore ricerca:', err);
              return of([]);
            })
          )
          : of([])
      ),
      tap(() => this.isLoading = false)
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

  get noResults(): boolean {
    const value = this.searchControl.value;
    return this.isDropdownOpen && !this.isLoading && this.searchResults.length === 0 && !!value && value.length > 1;
  }

  @HostListener('document:click', ['$event'])
  clickOutside(event: MouseEvent) {
    if (!this.eRef.nativeElement.contains(event.target)) {
      this.isDropdownOpen = false;
    }
  }
  onFocusOut(event: FocusEvent): void {
    const relatedTarget = event.relatedTarget as HTMLElement | null;

    // Se non stai andando su un elemento figlio della search-box, chiudi il dropdown
    if (!this.eRef.nativeElement.contains(relatedTarget)) {
      this.isDropdownOpen = false;
    }
  }


}
