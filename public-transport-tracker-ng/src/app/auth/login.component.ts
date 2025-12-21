import { Component } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from './auth.service';

@Component({
    selector: 'app-login',
    standalone: true,
    imports: [ReactiveFormsModule],
    templateUrl: './login.component.html',
    styleUrls: ['./login.component.css']
})
export class LoginComponent {

    form!: FormGroup;

    loading = false;
    error?: string;

    constructor(
        private fb: FormBuilder,
        private authService: AuthService,
        private router: Router
    ) {
        this.form = this.fb.nonNullable.group({
            identifier: ['', Validators.required],
            identifierType: ['username', Validators.required],
            password: ['', Validators.required]
        });
    }

    login(): void {
        if (this.form.invalid) {
            return;
        }

        this.loading = true;
        this.error = undefined;

        this.authService.login(this.form.getRawValue())
            .subscribe({
                next: () => {
                    this.loading = false;
                    this.router.navigate(['/']);
                },
                error: () => {
                    this.loading = false;
                    this.error = 'Login fehlgeschlagen';
                }
            });
    }
}
