import { Component, OnInit } from '@angular/core';
import { FaqService } from '../../services/faq.service';
import { Faq } from '../../models/faq.model';
import {NgForOf, NgIf} from '@angular/common';

@Component({
  selector: 'app-faq',
  templateUrl: './faq.component.html',
  standalone: true,
  imports: [
    NgForOf,
    NgIf
  ],
  styleUrls: ['./faq.component.css']
})
export class FaqComponent implements OnInit {
  faqs: Faq[] = [];

  constructor(private faqService: FaqService) {}

  ngOnInit(): void {
    this.faqService.getFaqs().subscribe(data => {
      console.log("FAQ ricevute:", data);
      this.faqs = data.map(faq => ({ ...faq, open: false })); // aggiunge proprietà open
    });
  }
  toggleFaq(faq: any) {
    faq.open = !faq.open;
  }


}
