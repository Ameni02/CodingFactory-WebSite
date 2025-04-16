import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot, Router, UrlTree } from '@angular/router';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AuthGuard implements CanActivate {
  constructor(private router: Router) {}

  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): boolean | UrlTree | Observable<boolean | UrlTree> | Promise<boolean | UrlTree> {
    // Skip guard for public routes
    const publicRoutes = ['/login', '/register', '/activate-account', '/forgot-password', '/reset-password', '/unban-req'];
    if (publicRoutes.some(publicRoute => state.url.startsWith(publicRoute))) {
      return true;
    }

    const token = localStorage.getItem('authToken');

    if (!token) {
      return this.router.createUrlTree(['/login'], {
        queryParams: { returnUrl: state.url }
      });
    }

    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      const userRole = this.extractRole(payload);
      const requestedRoute = state.url.split('/')[1];

      // Allow access to modify-user for all authenticated users
      if (requestedRoute === 'modify-user') {
        return true;
      }

      // Admin routes
      if (userRole === 'ADMIN' && requestedRoute === 'admin') {
        return true;
      }

      // User routes
      if (userRole === 'USER' && (requestedRoute === 'home' || requestedRoute === 'pfe-space')) {
        return true;
      }

      // Redirect based on role
      const targetRoute = userRole === 'ADMIN' ? '/admin' : '/home';
      return this.router.createUrlTree([targetRoute]);

    } catch (error) {
      console.error('Error decoding token', error);
      localStorage.removeItem('authToken');
      return this.router.createUrlTree(['/login']);
    }
  }

  private extractRole(payload: any): string {
    if (payload?.authorities) {
      if (Array.isArray(payload.authorities)) {
        return payload.authorities.includes('ADMIN') ? 'ADMIN' :
          payload.authorities.includes('USER') ? 'USER' : '';
      }
    }
    return '';
  }
}
