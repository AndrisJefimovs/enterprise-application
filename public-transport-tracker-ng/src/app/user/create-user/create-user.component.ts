import { Component, Input, OnInit } from "@angular/core";
import { UserService } from "../user.service";
import { ActivatedRoute, Router, RouterLink } from "@angular/router";
import { IUser } from "../model/user";
import { FormArray, FormBuilder, FormGroup, ReactiveFormsModule, ValidatorFn, Validators } from "@angular/forms";
import { AuthService } from "../../auth/auth.service";
import { fieldsEqual } from "../../core/fields-equal.validator";

@Component({
    selector: 'app-create-user',
    standalone: true,
    imports: [
        ReactiveFormsModule,
        RouterLink
    ],
    templateUrl: './create-user.component.html',
    styleUrl: './create-user.component.css'
})
export class CreateUserComponent implements OnInit {

    public loading = false;
    public form!: FormGroup;
    public user: IUser = {};
    public error?: string;
    public showPasswordFields: boolean = false;
    public permissions: string[] = [];

    private passwordMatchValidator: ValidatorFn = fieldsEqual('password', 'passwordConfirm');

    constructor(
        private fb: FormBuilder,
        private router: Router,
        private route: ActivatedRoute,
        private authService: AuthService,
        private userService: UserService
    ) {}

    public ngOnInit(): void {
        this.initForm();
    }

    private buildPermissionsArray(): FormArray {
        // copy permissions
        this.permissions = this.authService.permissions.slice();
        
        return this.fb.array(
            // on default all permissions are disabled
            this.permissions.map(p => this.fb.control(false))
        );
    }

    public initForm(): void {
        this.form = this.fb.group({
            username: ['', [Validators.required, Validators.maxLength(24)]],
            email: ['', [Validators.required, Validators.email, Validators.maxLength(127)]],
            password: ['', [Validators.required, Validators.minLength(4), Validators.maxLength(255)]],
            passwordConfirm: ['', [Validators.required, Validators.minLength(4), Validators.maxLength(255)]],
            permissions: this.buildPermissionsArray(),
            loginEnabled: this.fb.control(true)
        });

        this.form.addValidators(this.passwordMatchValidator);
    }

    public update(): void {
        this.error = undefined;
        const formValue = this.form.value;

        if (this.form.invalid) {
            if (this.form.errors?.['fieldsNotEqual']) {
                this.error = 'Die Passwörter stimmen nicht überein.';
            }
            return;
        }

        this.loading = true;

        const rawPermissions: boolean[] = this.form.getRawValue().permissions;
        const selectedPermissions = rawPermissions
            .map((checked: boolean, i: number) =>
                checked ? this.permissions[i] : null
            )
            .filter((v: string | null) => v !== null);

        const user: IUser = structuredClone(this.user);
        user.username = formValue.username;
        user.email = formValue.email;
        user.permissions = selectedPermissions;
        user.password = formValue.password;
        user.loginEnabled = formValue.loginEnabled;

        this.userService.createUser(user).subscribe({
            next: (res) => {
                if (res.body) {
                    this.router.navigate([`/user/${res.body.id!}`]);
                }
            },
            error: (err) => {
                if (err.status === 400) {
                    this.error = "Die request ist fehlerhaft!";
                }
                else if (err.status === 403) {
                    this.error = "Die request wurde verboten!";
                }
                else if (err.status == 500) {
                    this.error = "Der user konte auf Grund eines Server-Fehlers nicht gespeichert werden."
                }
                else {
                    this.error = "Es ist ein Fehler aufgetreten: " + err.message;
                }
                this.loading = false;
            },
            complete: () => {
                this.loading = false;
            }
        });
    }

}