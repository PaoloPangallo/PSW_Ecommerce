import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Product } from '../../../models/product.model';
import { AdminProductService } from '../../../service-admin/admin-product.service';
import {LirePipe} from '../../../services/lire.pipe';

@Component({
  selector: 'app-admin-products',
  templateUrl: './admin-products.component.html',
  styleUrls: ['./admin-products.component.css'],
  standalone: true,
  imports: [CommonModule, FormsModule, LirePipe]
})
export class AdminProductsComponent implements OnInit {
  products: Product[] = [];
  editingProduct: Product | null = null;

  // Modalità creazione: newProduct come Partial<Product> (senza ID)
  createMode = false;
  newProduct: Partial<Product> = {
    description: '',
    stock: 0,  // campo per la quantità
    name: '',
    price: 0
  };

  constructor(private adminProductService: AdminProductService) {}

  ngOnInit(): void {
    this.loadProducts();
  }

  loadProducts(): void {
    this.adminProductService.getProducts().subscribe(
      data => this.products = data,
      error => console.error('Errore nel recupero prodotti', error)
    );
  }

  onEdit(product: Product): void {
    this.editingProduct = { ...product };
  }

  cancelEdit(): void {
    this.editingProduct = null;
  }

  onSubmitUpdate(): void {
    if (this.editingProduct) {
      this.adminProductService.updateProduct(this.editingProduct.id, this.editingProduct)
        .subscribe(
          updated => {
            const index = this.products.findIndex(p => p.id === updated.id);
            if (index !== -1) {
              this.products[index] = updated;
            }
            this.editingProduct = null;
          },
          error => console.error('Errore aggiornamento prodotto', error)
        );
    }
  }

  onDelete(product: Product): void {
    if (confirm(`Eliminare il prodotto ${product.name}?`)) {
      this.adminProductService.deleteProduct(product.id).subscribe(
        () => {
          this.products = this.products.filter(p => p.id !== product.id);
        },
        error => console.error('Errore eliminazione prodotto', error)
      );
    }
  }

  // Apertura della modalità di creazione
  onCreate(): void {
    this.createMode = true;
    this.newProduct = {
      description: '',
      stock: 0,  // Inizializza la quantità a 0 (o a un valore di default)
      name: '',
      price: 0
    };
  }

  cancelCreate(): void {
    this.createMode = false;
  }

  onSubmitCreate(): void {
    // Invia newProduct senza ID (il backend genererà l'ID)
    this.adminProductService.createProduct(this.newProduct as Product).subscribe(
      createdProduct => {
        this.products.push(createdProduct);
        this.createMode = false;
      },
      error => console.error('Errore nella creazione del prodotto', error)
    );
  }
}
