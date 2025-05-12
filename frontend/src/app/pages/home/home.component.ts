import { Component, OnInit, inject } from '@angular/core';
import {CommonModule, NgOptimizedImage} from '@angular/common';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { ProductService } from '../../services/product.service';
import { Product } from '../../models/product.model';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule, NgOptimizedImage],
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {
  // Lista dei prodotti in evidenza (da visualizzare nella home)
  featuredProducts: Product[] = [];
  // Lista completa (se necessario in futuro)
  allProducts: Product[] = [];

  // Informazioni relative ai servizi offerti
  services = [
    { title: 'Spedizione Gratis', description: 'Per ordini sopra i 50€', icon: 'assets/icons/shipping-icon.png' },
    { title: 'Assistenza 24/7', description: 'Chat o telefono sempre attivi', icon: 'assets/icons/support-icon.png' },
    { title: 'Pagamenti Sicuri', description: 'Criptati e protetti', icon: 'assets/icons/payment-icon.png' }
  ];

  // Variabili per l'image generator
  productName: string = 'Prodotto';
  prompt: string = '';
  loading: boolean = false;
  errorMsg: string = '';
  generatedImageUrl: string = '';

  // Iniezione del ProductService
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
      // Qui potresti chiamare un servizio per gestire l'iscrizione
    }
  }

  onGenerate(): void {
    this.loading = true;
    this.errorMsg = '';
    // Simulazione di una chiamata asincrona per generare l'immagine
    setTimeout(() => {
      if (this.prompt.trim() === '') {
        this.errorMsg = 'Inserisci un prompt valido.';
        this.loading = false;
      } else {
        // In un caso reale chiameresti un servizio HTTP per generare l'immagine
        this.generatedImageUrl = 'https://example.com/path/to/generated/image.jpg';
        this.loading = false;
      }
    }, 2000);
  }

  goBack(): void {
    window.history.back();
  }


  // home.component.ts (ngAfterViewInit)
  ngAfterViewInit() {
    const video = document.getElementById('promoVideo') as HTMLVideoElement;
    const button = document.getElementById('replayButton') as HTMLButtonElement;

    video.addEventListener('ended', () => {
      button.style.display = 'block';
    });

    button.addEventListener('click', () => {
      video.currentTime = 0;
      video.play();
      button.style.display = 'none';
    });
  }




}
