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
        path: 'user/:id',
        canActivate: [AuthGuard],
        loadComponent: () => import('./user/user-details/user-details.component')
            .then(c => c.UserDetailsComponent)
    },
    {
        path: 'edit-user/:id',
        canActivate: [AuthGuard],
        loadComponent: () => import('./user/edit-user/edit-user.component')
            .then(c => c.EditUserComponent)
    },
    {
        path: 'create-user',
        canActivate: [AuthGuard],
        loadComponent: () => import('./user/create-user/create-user.component')
            .then(c => c.CreateUserComponent)
    },
    {
        path: 'welcome',
        loadComponent: () => import('./welcome/welcome.component')
            .then(c => c.LoginComponent)
    },
    {
        path: 'nearby',
        loadComponent: () => import('./trip/nearby-trips/nearby-trips.component')
            .then(c => c.NearbyTripsComponent)
    }
];
