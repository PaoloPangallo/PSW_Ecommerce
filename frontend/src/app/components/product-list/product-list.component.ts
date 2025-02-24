import { Component, OnInit, inject } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { ProductService } from '../../services/product.service';
import { Product } from '../../models/product.model';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { combineLatest } from 'rxjs';

@Component({
  selector: 'app-product-list',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './product-list.component.html',
  styleUrls: ['./product-list.component.css']
})
export class ProductListComponent implements OnInit {
  products: Product[] = [];
  // Se la rotta include una categoria, la memorizziamo qui
  category: string | null = null;
  // Se c'è un query param 'search', lo memorizziamo qui
  searchTerm: string = '';

  private route = inject(ActivatedRoute);

  constructor(private productService: ProductService) {}

  ngOnInit(): void {
    // Ascoltiamo contemporaneamente i parametri della rotta e i query param
    combineLatest([this.route.paramMap, this.route.queryParams]).subscribe(
      ([params, queryParams]) => {
        // Leggiamo 'categoryName' dai parametri della rotta
        const encodedCategory = params.get('categoryName');
        this.category = encodedCategory ? decodeURIComponent(encodedCategory) : null;
        // Leggiamo 'search' dai query param, se presente
        this.searchTerm = queryParams['search'] || '';

        console.log("✅ Categoria ricevuta:", this.category);
        console.log("✅ Termine di ricerca ricevuto:", this.searchTerm);

        // Carichiamo i prodotti in base alla categoria e/o al termine di ricerca
        this.loadProducts();
      }
    );
  }

  loadProducts(): void {
    if (this.category) {
      // Se c'è una categoria, chiamiamo il servizio per i prodotti di quella categoria
      this.productService.getProductsByCategory(this.category).subscribe(
        (data) => {
          this.products = this.applySearchFilter(data);
        },
        (error) => {
          console.error(`Errore nel recupero dei prodotti per la categoria ${this.category}:`, error);
        }
      );
    } else {
      // Altrimenti, recuperiamo tutti i prodotti
      this.productService.getAllProducts().subscribe(
        (data) => {
          this.products = this.applySearchFilter(data);
        },
        (error) => {
          console.error('Errore nel recupero dei prodotti:', error);
        }
      );
    }
  }

  // Metodo per applicare il filtro di ricerca sull'array di prodotti
  private applySearchFilter(data: Product[]): Product[] {
    if (!this.searchTerm) {
      return data;
    }
    return data.filter(product =>
      product.name.toLowerCase().includes(this.searchTerm.toLowerCase())
    );
  }
}
