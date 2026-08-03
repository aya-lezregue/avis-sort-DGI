import { Routes } from '@angular/router';
import { LoginComponent } from './auth/login/login.component';
import { MainLayoutComponent } from './shared/layout/main-layout.component';
import { AdminDashboardComponent } from './admin/dashboard/admin-dashboard.component';
import { AdminCabsComponent } from './admin/cabs/admin-cabs.component';
import { AdminUsersComponent } from './admin/users/admin-users.component';
import { PosteDashboardComponent } from './poste/dashboard/poste-dashboard.component';
import { PosteCabsComponent } from './poste/cabs/poste-cabs.component';
import { PosteUploadComponent } from './poste/upload/poste-upload.component';
import { DgiDashboardComponent } from './dgi/dashboard/dgi-dashboard.component';
import { DgiDepotComponent } from './dgi/depot/dgi-depot.component';
import { DgiRechercheComponent } from './dgi/recherche/dgi-recherche.component';
import { DgiFichiersComponent } from './dgi/fichiers/dgi-fichiers.component';
import { authGuard } from './core/guards/auth.guard';
import { roleGuard } from './core/guards/role.guard';

export const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: '', redirectTo: '/login', pathMatch: 'full' },

  {
    path: 'admin',
    component: MainLayoutComponent,
    canActivate: [authGuard, roleGuard(['ADMIN_POSTE'])],
    children: [
      { path: '', component: AdminDashboardComponent },
      { path: 'cabs', component: AdminCabsComponent },
      { path: 'users', component: AdminUsersComponent }
    ]
  },

  {
    path: 'poste',
    component: MainLayoutComponent,
    canActivate: [authGuard, roleGuard(['USER_POSTE'])],
    children: [
      { path: '', component: PosteDashboardComponent },
      { path: 'cabs', component: PosteCabsComponent },
      { path: 'upload', component: PosteUploadComponent }
    ]
  },

  {
    path: 'dgi',
    component: MainLayoutComponent,
    canActivate: [authGuard, roleGuard(['DGI_USER'])],
    children: [
      { path: '', component: DgiDashboardComponent },
      { path: 'depot', component: DgiDepotComponent },
      { path: 'recherche', component: DgiRechercheComponent },
      { path: 'fichiers', component: DgiFichiersComponent }
    ]
  },

  { path: '**', redirectTo: '/login' }
];