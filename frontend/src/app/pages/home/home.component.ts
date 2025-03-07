import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { ProductService } from '../../services/product.service';
import { Product } from '../../models/product.model';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {
  featuredProducts: Product[] = [];
  allProducts: Product[] = []; // Aggiunto per la lista completa di prodotti

  // Dati statici per categorie e servizi
  categories = [
    { name: 'Smartphone e Telefonia', icon: 'assets/icons/smartphone-icon.png', link: '/category/Smartphone e Telefonia' },
    { name: 'TV e Home Entertainment', icon: 'assets/icons/tv-icon.png', link: '/category/TV e Home Entertainment' },
    { name: 'Informatica e Accessori', icon: 'assets/icons/laptop-icon.png', link: '/category/Informatica e Accessori' },
    { name: 'Elettrodomestici', icon: 'assets/icons/home-icon.png', link: '/category/Elettrodomestici' }
  ];

  services = [
    { title: 'Spedizione Gratis', description: 'Per ordini sopra i 50€', icon: 'assets/icons/shipping-icon.png' },
    { title: 'Assistenza 24/7', description: 'Chat o telefono sempre attivi', icon: 'assets/icons/support-icon.png' },
    { title: 'Pagamenti Sicuri', description: 'Criptati e protetti', icon: 'assets/icons/payment-icon.png' }
  ];

  private productService = inject(ProductService);

  ngOnInit(): void {
    this.loadFeaturedProducts();
  }

  loadFeaturedProducts(): void {
    this.productService.getFeaturedProducts().subscribe({
      next: (products) => {
        this.featuredProducts = products;
        console.log("Prodotti in evidenza ricevuti:", products);
      },
      error: (err) => {
        console.error('Errore nel caricamento dei prodotti in evidenza:', err);
      }
    });
  }



  subscribeToNewsletter(event: Event): void {
    event.preventDefault();
    const form = event.target as HTMLFormElement;
    const emailInput = form.querySelector('input[type="email"]') as HTMLInputElement;
    const email = emailInput.value.trim();
    if (email) {
      console.log('Iscrizione newsletter, email:', email);
      emailInput.value = '';
    }
  }
}
