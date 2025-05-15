import { Component, OnInit } from '@angular/core';
import {ActivatedRoute, RouterLink} from '@angular/router';
import { NewsletterService } from '../../services/newsletter.service';

@Component({
  selector: 'app-newsletter-confirm',
  standalone: true,
  templateUrl: './newsletter-confirm.component.html',
  imports: [
    RouterLink
  ],
  styleUrls: ['./newsletter-confirm.component.css']
})
export class NewsletterConfirmComponent implements OnInit {
  message: string = '⏳ Verifica in corso...';
  success: boolean = false;

  constructor(
    private route: ActivatedRoute,
    private newsletterService: NewsletterService
  ) {}

  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      const token = params['token'];
      if (token) {
        this.newsletterService.confirm(token).subscribe({
          next: res => {
            this.success = true;
            this.message = res.message || '✅ Iscrizione confermata con successo!';
          },
          error: err => {
            this.success = false;
            this.message = err?.error?.error || '❌ Token non valido o già usato.';
          }
        });
      } else {
        this.success = false;
        this.message = '❌ Nessun token trovato nel link.';
      }
    });
  }
}
