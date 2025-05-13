import { Component, OnInit, AfterViewInit, Inject, inject } from '@angular/core';
import { CommonModule, isPlatformBrowser, NgOptimizedImage } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { PLATFORM_ID } from '@angular/core';

import { ProductService } from '../../services/product.service';
import { Product } from '../../models/product.model';
import {HomeRecommendationsComponent} from '../../components/home-recommendations/home-recommendations.component';
import {AuthService} from '../../services/auth.services';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule, NgOptimizedImage, HomeRecommendationsComponent],
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit, AfterViewInit {
  featuredProducts: Product[] = [];
  allProducts: Product[] = [];

  services = [
    { title: 'Spedizione Gratis', description: 'Per ordini sopra i 50€', icon: 'assets/icons/shipping-icon.png' },
    { title: 'Assistenza 24/7', description: 'Chat o telefono sempre attivi', icon: 'assets/icons/support-icon.png' },
    { title: 'Pagamenti Sicuri', description: 'Criptati e protetti', icon: 'assets/icons/payment-icon.png' }
  ];

  productName: string = 'Prodotto';
  prompt: string = '';
  loading: boolean = false;
  errorMsg: string = '';
  generatedImageUrl: string = '';

  private productService = inject(ProductService);

  constructor(
    @Inject(PLATFORM_ID) private platformId: Object,
    public authService: AuthService  // ✅ aggiunto
  ) {}

  ngOnInit(): void {
    this.loadFeaturedProducts();
  }

  ngAfterViewInit(): void {
    if (isPlatformBrowser(this.platformId)) {
      const video = document.getElementById('promoVideo') as HTMLVideoElement;
      const button = document.getElementById('replayButton') as HTMLButtonElement;

      video?.addEventListener('ended', () => {
        if (button) button.style.display = 'block';
      });

      button?.addEventListener('click', () => {
        video.currentTime = 0;
        video.play();
        button.style.display = 'none';
      });
    }
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
    const email = emailInput?.value.trim();
    if (email) {
      console.log('Iscrizione newsletter, email:', email);
      emailInput.value = '';
      // chiamata a backend qui se servisse
    }
  }

  onGenerate(): void {
    this.loading = true;
    this.errorMsg = '';

    setTimeout(() => {
      if (this.prompt.trim() === '') {
        this.errorMsg = 'Inserisci un prompt valido.';
        this.loading = false;
      } else {
        this.generatedImageUrl = 'https://example.com/path/to/generated/image.jpg';
        this.loading = false;
      }
    }, 2000);
  }

  goBack(): void {
    if (isPlatformBrowser(this.platformId)) {
      window.history.back();
    }
  }
}
