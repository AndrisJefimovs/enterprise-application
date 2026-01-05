import { Routes } from '@angular/router';
import { AuthGuard } from './auth/auth.guard';
import { WelcomeGuard } from './welcome/welcome.guard';

export const routes: Routes = [
    // redirect default to welcome
    {
        path: '',
        redirectTo: 'welcome',
        pathMatch: 'full'
    },

    // paths
    {
        path: 'welcome',
        loadComponent: () => import('./welcome/welcome.component')
            .then(c => c.LoginComponent)
    },
    {
        path: 'home',
        canActivate: [WelcomeGuard],
        loadComponent: () => import('./home/home.component')
            .then(c => c.HomeComponent)
    },
    {
        path: 'login',
        canActivate: [WelcomeGuard],
        loadComponent: () => import('./auth/login/login.component')
            .then(c => c.LoginComponent)
    },
    {
        path: 'register',
        canActivate: [WelcomeGuard],
        loadComponent: () => import('./auth/register/register.component')
            .then(c => c.RegisterComponent)
    },
    {
        path: 'users',
        canActivate: [WelcomeGuard, AuthGuard],
        loadComponent: () => import('./user/users/users.component')
            .then(c => c.UsersComponent)
    },
    {
        path: 'user/:id',
        canActivate: [WelcomeGuard, AuthGuard],
        loadComponent: () => import('./user/user-details/user-details.component')
            .then(c => c.UserDetailsComponent)
    },
    {
        path: 'edit-user/:id',
        canActivate: [WelcomeGuard, AuthGuard],
        loadComponent: () => import('./user/edit-user/edit-user.component')
            .then(c => c.EditUserComponent)
    },
    {
        path: 'create-user',
        canActivate: [WelcomeGuard, AuthGuard],
        loadComponent: () => import('./user/create-user/create-user.component')
            .then(c => c.CreateUserComponent)
    },
    {
        path: 'nearby',
        canActivate: [WelcomeGuard],
        loadComponent: () => import('./trip/nearby-trips/nearby-trips.component')
            .then(c => c.NearbyTripsComponent)
    },

    // fallback
    {
        path: '**',
        redirectTo: 'welcome'
    }
];
