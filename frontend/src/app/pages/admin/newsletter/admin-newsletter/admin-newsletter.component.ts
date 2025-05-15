import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NewsletterService } from '../../../../services/newsletter.service';
import { NewsletterSubscriber } from '../../../../models/newsletter.model';

@Component({
  selector: 'app-admin-newsletter',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './admin-newsletter.component.html',
  styleUrls: ['./admin-newsletter.component.css']
})
export class AdminNewsletterComponent implements OnInit {
  subscribers: NewsletterSubscriber[] = []; // ✅ tipizzazione corretta
  loading = true;

  constructor(private newsletterService: NewsletterService) {}

  ngOnInit(): void {
    this.newsletterService.getAllSubscribers().subscribe({
      next: (res: NewsletterSubscriber[]) => { // tipizzato anche qui per sicurezza
        this.subscribers = res;
        this.loading = false;
      },
      error: err => {
        console.error('Errore nel caricamento iscritti:', err);
        this.loading = false;
      }
    });
  }

  sendNow(): void {
    this.newsletterService.sendNow().subscribe({
      next: () => alert('Newsletter inviata manualmente!'),
      error: () => alert('Errore durante l’invio.')
    });
  }
}
