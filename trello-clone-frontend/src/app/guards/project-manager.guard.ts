import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { UserRole } from '../models/user.model';

export const projectManagerGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  const currentUser = authService.getCurrentUser();

  if (currentUser && (currentUser.role === UserRole.PROJECT_MANAGER || currentUser.role === UserRole.ADMIN)) {
    return true;
  } else {
    router.navigate(['/dashboard']);
    return false;
  }
};
