import { Component, OnInit } from '@angular/core';

import { MatDialog } from '@angular/material/dialog';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {Product} from '../../../models/product.model';
import {ProductService} from '../../../services/product.service';
import {LirePipe} from '../../../services/lire.pipe';
import {CommonModule} from '@angular/common';

@Component({
  selector: 'app-admin-discount',
  templateUrl: './admin-discount.component.html',
  styleUrls: ['./admin-discount.component.css'],
  imports: [
    LirePipe,
    ReactiveFormsModule,
     CommonModule, // 👈 necessario per *ngIf, *ngFor, ecc.

],
  standalone: true
})
export class AdminDiscountComponent implements OnInit {
  products: Product[] = [];
  selectedProduct: Product | null = null;
  discountForm!: FormGroup;
  showForm = false;

  constructor(
    private productService: ProductService,
    private fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.loadProducts();
    this.discountForm = this.fb.group({
      discountPercentage: [0, [Validators.required, Validators.min(0), Validators.max(100)]],
    });
  }

  loadProducts() {
    this.productService.getAllProducts().subscribe((data) => {
      this.products = data;
    });
  }

  openDiscountForm(product: Product) {
    this.selectedProduct = product;
    this.discountForm.patchValue({
      discountPercentage: product.discountPercentage || 0,
    });
    this.showForm = true;
  }

  cancel() {
    this.showForm = false;
    this.selectedProduct = null;
  }

  applyDiscount() {
    if (!this.selectedProduct) return;

    const updatedProduct = {
      ...this.selectedProduct,
      discountPercentage: this.discountForm.value.discountPercentage,
    };

    console.log('🔵 Sto per inviare:', updatedProduct); // 👈 DEBUG

    this.productService.updateProduct(updatedProduct).subscribe({
      next: (res) => {
        console.log('✅ Risposta OK dal backend:', res); // 👈 DEBUG
        this.loadProducts();
        this.cancel();
      },
      error: (err) => {
        console.error('❌ Errore dal backend:', err); // 👈 DEBUG
      }
    });
  }

}
