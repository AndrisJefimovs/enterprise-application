import { Routes } from '@angular/router';
import { AuthGuard } from './auth/auth.guard';

export const routes: Routes = [
    {
        path: 'login',
        loadComponent: () => import('./auth/login/login.component')
            .then(c => c.LoginComponent)
    },
    {
        path: 'register',
        loadComponent: () => import('./auth/register/register.component')
            .then(c => c.RegisterComponent)
    },
    {
        path: '',
        canActivate: [AuthGuard],
        loadComponent: () => import('./app')
            .then(m => m.App)
    },
    {
        path: 'users',
        canActivate: [AuthGuard],
        loadComponent: () => import('./user/user.component')
            .then(c => c.UserComponent)
    }
];
