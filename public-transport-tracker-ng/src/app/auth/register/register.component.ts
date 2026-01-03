import { Component } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, ValidatorFn, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../auth.service';
import { fieldsEqual } from '../../core/fields-equal.validator';

@Component({
    selector: 'app-register',
    standalone: true,
    imports: [
        ReactiveFormsModule,
        RouterLink
    ],
    templateUrl: './register.component.html',
    styleUrls: ['./register.component.css']
})
export class RegisterComponent {

    public form!: FormGroup;
    
    public loading = false;
    public error?: string;

    private passwordMatchValidator: ValidatorFn = fieldsEqual('password', 'passwordConfirm');

    constructor(
        private fb: FormBuilder,
        private authService: AuthService,
        private router: Router
    ) {
        this.form = this.fb.nonNullable.group({
            username: ['', [Validators.required, Validators.maxLength(24)]],
            email: ['', [Validators.required, Validators.email, Validators.maxLength(127)]],
            password: ['', [Validators.required, Validators.maxLength(255), Validators.minLength(4)]],
            passwordConfirm: ['', [Validators.required, Validators.maxLength(255), Validators.minLength(4)]]
        });

        this.form.addValidators(this.passwordMatchValidator);
    }

    public register(): void {
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
