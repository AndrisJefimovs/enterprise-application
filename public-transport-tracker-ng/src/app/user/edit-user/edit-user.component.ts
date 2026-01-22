import { Component, Input, OnInit } from "@angular/core";
import { UserService } from "../user.service";
import { ActivatedRoute, Router, RouterLink } from "@angular/router";
import { IUser } from "../model/user";
import { FormArray, FormBuilder, FormGroup, ReactiveFormsModule, ValidatorFn, Validators } from "@angular/forms";
import { AuthService } from "../../auth/auth.service";
import { combineLatest, take } from "rxjs";
import { fieldsEqual } from "../../core/fields-equal.validator";

@Component({
    selector: 'app-edit-user',
    standalone: true,
    imports: [
        ReactiveFormsModule,
        RouterLink
    ],
    templateUrl: './edit-user.component.html',
    styleUrl: './edit-user.component.css'
})
export class EditUserComponent implements OnInit {
    
    @Input() public userId?: number;

    public loading = false;
    public form!: FormGroup;
    public user: IUser = {};
    public error?: string;
    public showPasswordFields: boolean = false;
    public allPermissions: string[] = [];

    private passwordMatchValidator: ValidatorFn = fieldsEqual('password', 'passwordConfirm');

    constructor(
        private fb: FormBuilder,
        private router: Router,
        private route: ActivatedRoute,
        private authService: AuthService,
        private userService: UserService
    ) {}

    public ngOnInit(): void {
        // if component is embedded
        if (this.userId) {
            this.init(this.userId);
        }
        else {
            this.route.paramMap.subscribe(params => {
                const id = params.get('id');
                if (id) {
                    this.init(Number(id));
                }
            });
        }
        
    }


    private init(userId: number): void {
        if (userId !== this.authService.getId() && !this.authService.hasPermission('UPDATE_USER')) {
            this.router.navigate(['/users']);
            return;
        }

        this.userService.getUser(userId).subscribe((editedUser) => {
            this.user = editedUser;
            this.initForm();
        });
    }


    private buildPermissionsArray(): FormArray {
        return this.fb.array(
            this.allPermissions.filter(p => p !== "LOGIN").map(permission => {
                const control = this.fb.control(
                    this.user.permissions?.includes(permission)
                );

                if (!this.authService.permissions.includes(permission)) {
                    control.disable();
                }

                return control;
            })
        );
    }

    public initForm(): void {
        // add all permissions the user and the user that is being edited have
        this.allPermissions = this.authService.permissions.slice(); // clone array 
        for (const permission of this.user.permissions ?? []) {
            if (!this.allPermissions.includes(permission)) {
                this.allPermissions.push(permission);
            }
        }

        this.form = this.fb.group({
            username: [this.user.username, [Validators.required, Validators.maxLength(24)]],
            email: [this.user.email, [Validators.required, Validators.email, Validators.maxLength(127)]],
            password: [null],
            passwordConfirm: [null],
            permissions: this.buildPermissionsArray(),
            loginEnabled: this.fb.control(this.user.permissions?.includes("LOGIN") ?? false)
        });

        // password fields are initially disabled
        this.form.get('password')?.disable();
        this.form.get('passwordConfirm')?.disable();
    }

    public enablePasswordChange(): void {
        this.showPasswordFields = true;

        const password = this.form.get('password')!;
        const confirm = this.form.get('passwordConfirm')!;

        password.enable();
        confirm.enable();

        password.setValidators([Validators.required, Validators.minLength(4), Validators.maxLength(255)]);
        confirm.setValidators(Validators.required);

        this.form.addValidators(this.passwordMatchValidator);

        this.form.updateValueAndValidity();
    }

    public disablePasswordChange(): void {
        this.showPasswordFields = false;

        const password = this.form.get('password')!;
        const passwordConfirm = this.form.get('passwordConfirm')!;

        password.reset();
        passwordConfirm.reset();

        password.setValue(null);
        passwordConfirm.setValue(null);

        password.clearValidators();
        passwordConfirm.clearValidators();

        password.disable();
        passwordConfirm.disable();

        this.form.removeValidators(
            this.passwordMatchValidator!
        );
    }

    private hasChanges(
        formValue: any,
        selectedPermissions: string[],
        user: IUser
    ): boolean {
        if (formValue.username !== user.username) return true;
        if (formValue.email !== user.email) return true;

        // Passwort nur prüfen, wenn eingegeben
        if (formValue.password && formValue.password.length > 0) return true;

        // Permissions vergleichen (reihenfolge-unabhängig)
        const oldPerms = new Set(user.permissions ?? []);
        const newPerms = new Set(selectedPermissions);

        if (oldPerms.size !== newPerms.size) return true;

        for (const p of oldPerms) {
            if (!newPerms.has(p)) return true;
        }

        return false;
    }

    public update(): void {
        this.error = undefined;
        const formValue = this.form.value;

        this.loading = true;

        const rawPermissions: boolean[] = this.form.getRawValue().permissions;

        const selectedPermissions = rawPermissions
            .map((checked: boolean, i: number) =>
                checked ? this.allPermissions[i] : null
            )
            .filter((v: string | null) => v !== null);
        if (formValue.loginEnabled) {
            selectedPermissions.push("LOGIN")
        }

        // do nothing if nothing was changed
        if (!this.hasChanges(formValue, selectedPermissions, this.user)) {
            this.loading = false;
            this.router.navigate([`/user/${this.user.id!}`]);
            return;
        }

        if (this.form.invalid) {
            if (this.form.errors?.['fieldsNotEqual']) {
                this.error = 'Die Passwörter stimmen nicht überein.';
            }
            this.loading = false;
            return;
        }

        const user: IUser = {id: this.user.id};
        if (this.user.username !== formValue.username) {
            user.username = formValue.username;
        }
        if (this.user.email !== formValue.email) {
            user.email = formValue.email;
        }
        if (this.user.permissions !== selectedPermissions) {
            user.permissions = selectedPermissions;
        }
        user.password = formValue.password;

        this.userService.updateUser(user).subscribe({
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
                this.userService.getUser(this.user.id!).subscribe(
                    (data) => this.user = data
                )
                this.loading = false;
            },
            complete: () => {
                this.loading = false;
                this.user = user
            }
        });
    }

    public reset(): void {
        this.form.get('username')?.setValue(this.user.username);
        this.form.get('email')?.setValue(this.user.email);
        this.form.setControl('permissions', this.buildPermissionsArray());
        this.form.get('loginEnabled')?.setValue(this.user.permissions?.includes("LOGIN") ?? false);

        this.disablePasswordChange();

        // reset form state
        this.form.markAsPristine();
        this.form.markAsUntouched();
    }

}