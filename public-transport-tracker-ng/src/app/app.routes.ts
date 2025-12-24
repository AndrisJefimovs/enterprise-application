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
        loadComponent: () => import('./user/users/users.component')
            .then(c => c.UsersComponent)
    },
    {
        path: 'users/:id',
        canActivate: [AuthGuard],
        loadComponent: () => import('./user/user-details/user-details.component')
            .then(c => c.UserDetailsComponent)
    },
    {
        path: 'welcome',
        loadComponent: () => import('./welcome/welcome.component')
            .then(c => c.LoginComponent)
    }
];
