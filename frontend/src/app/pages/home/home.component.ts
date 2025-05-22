import { Component, OnInit, AfterViewInit, Inject, inject } from '@angular/core';
import { CommonModule, isPlatformBrowser, NgOptimizedImage } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { PLATFORM_ID } from '@angular/core';

import { ProductService } from '../../services/product.service';
import { Product } from '../../models/product.model';
import {HomeRecommendationsComponent} from '../../components/home-recommendations/home-recommendations.component';
import {AuthService} from '../../services/auth.services';
import {NewsletterSignupComponent} from '../../components/newsletter-signup/newsletter-signup.component';
import {FooterComponent} from '../../components/footer/footer.component';
import {ChatBotComponent} from '../../components/chat-bot/chat-bot.component';
import {LirePipe} from '../../services/lire.pipe';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    FormsModule,
    NgOptimizedImage,
    HomeRecommendationsComponent,
    NewsletterSignupComponent,
    FooterComponent,
    ChatBotComponent,
    LirePipe
  ],

  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss']
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
  private productService = inject(ProductService);

  constructor(
    @Inject(PLATFORM_ID) private platformId: Object,
    public authService: AuthService  // ✅ aggiunto
  ) {}


  showChat = false;

  toggleChat() {
    this.showChat = !this.showChat;
  }


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
}
