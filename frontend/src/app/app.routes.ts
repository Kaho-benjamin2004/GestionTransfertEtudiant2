import { Routes } from '@angular/router';
import { LoginComponent, RegisterComponent } from './features/auth/components';
import { DashboardComponent } from './features/dashboard/dashboard.component';
import { ProfileComponent } from './features/dashboard/profile.component';
import { EtudiantComponent } from './features/etudiant/etudiant.component';
import { AdminComponent } from './features/admin/admin.component';
import { AuthGuard, NoAuthGuard } from './core/guards';
import { UnauthorizedComponent, LayoutComponent } from './shared/components';

export const routes: Routes = [
  {
    path: '',
    redirectTo: '/dashboard',
    pathMatch: 'full'
  },
  {
    path: 'auth',
    component: LayoutComponent,
    canActivate: [NoAuthGuard],
    children: [
      {
        path: 'login',
        component: LoginComponent
      },
      {
        path: 'register',
        component: RegisterComponent
      }
    ]
  },
  {
    path: 'login',
    component: LoginComponent,
    canActivate: [NoAuthGuard]
  },
  {
    path: 'register',
    component: RegisterComponent,
    canActivate: [NoAuthGuard]
  },
  {
    path: 'dashboard',
    component: DashboardComponent,
    canActivate: [AuthGuard]
  },
  {
    path: 'profile',
    component: ProfileComponent,
    canActivate: [AuthGuard]
  },
  {
    path: 'etudiant',
    component: EtudiantComponent,
    canActivate: [AuthGuard],
    data: { roles: ['ETUDIANT'] }
  },
  {
    path: 'admin',
    component: AdminComponent,
    canActivate: [AuthGuard],
    data: { roles: ['ADMIN'] }
  },
  {
    path: 'unauthorized',
    component: UnauthorizedComponent
  },
  {
    path: '**',
    redirectTo: '/dashboard'
  }
];
