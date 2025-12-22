import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../auth.service';

@Component({
    selector: 'app-login',
    standalone: true,
    imports: [
        ReactiveFormsModule,
        RouterLink
    ],
    templateUrl: './login.component.html',
    styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {

    form!: FormGroup;

    loading = false;
    error?: string;
    label: string = '';
    placeholder: string = '';
    inputType: 'text' | 'email' = 'text';

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

    ngOnInit(): void {
        // Auf Änderungen des Radiobuttons reagieren
        this.form.get('identifierType')!.valueChanges.subscribe(value => {
            this.updateText(value);
        });

        // Initial setzen
        this.updateText(this.form.get('identifierType')!.value);
    }

    updateText(value: string) {
        const identifierCtrl = this.form.get('identifier');
        if (!identifierCtrl) return;
        
        switch (value) {
            case "username":
                this.placeholder = "User";
                this.label = "Username";
                this.inputType = "text";
                identifierCtrl.setValidators([
                    Validators.required
                ]);
                break;
            case "email":
                this.placeholder = "deine@email.net";
                this.label = "Email-Adresse";
                this.inputType = "email";
                identifierCtrl.setValidators([
                    Validators.required,
                    Validators.email
                ]);
                break;
        }

        identifierCtrl.updateValueAndValidity();
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
