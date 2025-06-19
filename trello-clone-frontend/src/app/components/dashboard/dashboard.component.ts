import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterOutlet } from '@angular/router';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatMenuModule } from '@angular/material/menu';
import { MatDividerModule } from '@angular/material/divider';
import { AuthService } from '../../services/auth.service';
import { User } from '../../models';
import { ProjectListComponent } from '../project-list/project-list.component';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [
    CommonModule,
    RouterOutlet,
    MatToolbarModule,
    MatButtonModule,
    MatIconModule,
    MatMenuModule,
    MatDividerModule,
    ProjectListComponent
  ],
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss']
})
export class DashboardComponent implements OnInit {
  currentUser: User | null = null;

  constructor(private readonly authService: AuthService,private readonly router: Router) {}

  ngOnInit(): void {
    this.authService.currentUser$.subscribe(user => {
      this.currentUser = user;
    });
  }

  logout(): void {
    this.authService.logout().subscribe({
      next: () => {
        // --- ESTE CÓDIGO SE EJECUTA DESPUÉS DE UN LOGOUT EXITOSO ---
        // (Es decir, el servidor respondió OK y el cliente limpió los tokens)
        
        console.log('Logout completado. Redirigiendo a la página de login.');
        
        // Es el lugar correcto para redirigir al usuario.
        this.router.navigate(['/login']);
      },
      error: (err) => {
        // Aunque el servicio ya limpia los datos del cliente en caso de error,
        // puedes mostrar una notificación al usuario si es necesario.
        console.error('Ocurrió un error en el servidor durante el logout, pero el cliente ha sido limpiado.', err);
        
        // También podrías redirigir aquí para asegurar que el usuario salga de la vista protegida.
        this.router.navigate(['/login']);
      }
    });
  }

  getUserDisplayName(): string {
    if (this.currentUser) {
      return `${this.currentUser.firstName} ${this.currentUser.lastName}`;
    }
    return '';
  }
}
