import { Routes } from '@angular/router';
import { AuthGuard } from './auth/auth.guard';

export const routes: Routes = [
    {
        path: 'login',
        loadComponent: () => import('./auth/login.component').then(c => c.LoginComponent)
    },
    {
        path: 'register',
        loadComponent: () => import('./auth/register.component').then(c => c.RegisterComponent)
    },
    {
        path: '',
        canActivate: [AuthGuard],
        loadComponent: () => import('./app').then(m => m.App)
    }
];
