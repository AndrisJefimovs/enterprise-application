import { AbstractControl, ValidationErrors, ValidatorFn } from "@angular/forms";

export function fieldsEqual(
    field1: string,
    field2: string
): ValidatorFn {
    return (group: AbstractControl): ValidationErrors | null => {
        console.log("validator running ...")
        const c1 = group.get(field1);
        const c2 = group.get(field2);

        if (!c1 || !c2 || c1.disabled || c2.disabled) {
            return null;
        }

        return c1.value === c2.value
            ? null
            : { fieldsNotEqual: true };
    };
}
