import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import { UserInfo } from '../../core/models';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss']
})
export class DashboardComponent implements OnInit {
  currentUser: UserInfo | null = null;
  sidebarOpen = true;

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.authService.getCurrentUser$().subscribe((user: UserInfo | null) => {
      this.currentUser = user;
    });
  }

  toggleSidebar(): void {
    this.sidebarOpen = !this.sidebarOpen;
  }

  logout(): void {
    const refreshToken = this.authService.getRefreshToken();
    if (refreshToken) {
      this.authService.logout(refreshToken).subscribe({
        next: () => {
          this.router.navigate(['/login']);
        }
      });
    }
  }

  navigateTo(path: string): void {
    this.router.navigate([path]);
  }
}
