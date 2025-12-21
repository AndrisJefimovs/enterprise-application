import { Component } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../auth.service';

@Component({
    selector: 'app-register',
    standalone: true,
    imports: [ReactiveFormsModule],
    templateUrl: './register.component.html',
    styleUrls: ['./register.component.css']
})
export class RegisterComponent {

    form!: FormGroup;

    loading = false;
    error?: string;

    constructor(
        private fb: FormBuilder,
        private authService: AuthService,
        private router: Router
    ) {
        this.form = this.fb.nonNullable.group({
            username: ['', [Validators.required, Validators.maxLength(63)]],
            email: ['', [Validators.required, Validators.email, Validators.maxLength(127)]],
            password: ['', [Validators.required, Validators.maxLength(255)]]
        });
    }

    register(): void {
        if (this.form.invalid) {
            return;
        }

        this.loading = true;
        this.error = undefined;

        this.authService.register(this.form.getRawValue())
            .subscribe({
                next: (res) => {
                    this.loading = false;

                    if (res.statusCode === 0) {
                        this.router.navigate(['/login']);
                    }
                    else {
                        this.error = res.statusMessage ?? 'Registrierung fehlgeschlagen';
                    }
                },
                error: () => {
                    this.loading = false;
                    this.error = 'Registrierung fehlgeschlagen';
                }
            });
    }
}
