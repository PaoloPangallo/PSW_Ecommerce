import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot, Router } from '@angular/router';
import {AuthService} from '../services/auth.services';

@Injectable({
  providedIn: 'root'
})
export class AdminGuard implements CanActivate {

  constructor(private authService: AuthService, private router: Router) {}

  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): boolean {
    // Verifica se l'utente è loggato e se ha il ruolo ADMIN
    if (this.authService.isLoggedIn() && this.authService.isAdmin()) {
      return true;
    } else {
      // Se non è admin, reindirizza ad esempio alla home
      this.router.navigate(['/']);
      return false;
    }
  }



}
