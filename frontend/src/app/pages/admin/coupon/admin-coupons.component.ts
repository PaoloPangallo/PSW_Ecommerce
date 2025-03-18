import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AdminCouponService } from '../../../service-admin/admin-coupon.service';
import {Coupon, CouponCreationDTO} from '../../../models/coupon.model';

@Component({
  selector: 'app-admin-coupons',
  templateUrl: './admin-coupons.component.html',
  styleUrls: ['./admin-coupons.component.css'],
  standalone: true,
  imports: [CommonModule, FormsModule]
})
export class AdminCouponsComponent implements OnInit {
  coupons: Coupon[] = [];
  createMode: boolean = false;

  // newCoupon viene definito come Partial<CouponCreationDTO> per consentire l'omissione dell'ID (generato dal backend)
  newCoupon: Partial<CouponCreationDTO> = {
    code: '',
    discountPercentage: 0,
    expirationDate: '',
    isActive: true,
    minOrderValue: 0,
    productIds: []
  };

  // Variabile per gestire l'input di una stringa di ID prodotto separati da virgola
  productIdsString: string = '';

  constructor(private adminCouponService: AdminCouponService) { }

  ngOnInit(): void {
    this.loadCoupons();
  }

  loadCoupons(): void {
    this.adminCouponService.getCoupons().subscribe(
      data => {
        console.log('Coupon ricevuti dal backend:', data);
        this.coupons = data;
      },
      error => console.error('Errore nel recupero dei coupon', error)
    );
  }


  onCreate(): void {
    this.createMode = true;
    this.newCoupon = {
      code: '',
      discountPercentage: 0,
      expirationDate: '',
      isActive: true,
      minOrderValue: 0,
      productIds: []
    };
    this.productIdsString = '';
  }

  cancelCreate(): void {
    this.createMode = false;
  }

  onSubmitCreate(): void {
    // Converte la stringa di input in un array di numeri
    if (this.productIdsString.trim() !== '') {
      this.newCoupon.productIds = this.productIdsString.split(',')
        .map(id => parseInt(id.trim()))
        .filter(id => !isNaN(id));
    } else {
      this.newCoupon.productIds = [];
    }
    // Invia il nuovo coupon al backend; il cast garantisce che il DTO abbia il tipo corretto
    this.adminCouponService.createCoupon(this.newCoupon as CouponCreationDTO).subscribe(
      createdCoupon => {
        this.coupons.push(createdCoupon);
        this.createMode = false;
      },
      error => console.error('Errore nella creazione del coupon', error)
    );
  }
}
