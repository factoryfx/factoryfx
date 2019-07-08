import { ValidationError } from "./ValidationError";
import {AttributeType} from "./AttributeType";

export class AttributeMetadata<T>  {
    public constructor(private en: string, private de: string, private attributeType: AttributeType, private isNullable: boolean, private possibleEnumValues: string[]) {
        this.attributeType=attributeType;
    }

    public getLabelText(locale: string): string{
        if (locale==='de'){
            return this.de;
        }
        return this.en;
    }

    validationFunction!:(root: T)=>ValidationError;
    validation(validationFunction:(root: T)=>ValidationError): void{
        this.validationFunction=validationFunction;
    }

    validate(value: T):ValidationError | null{
        if (!this.isNullable && !value){
            let workaround: any=value;
            if (!value && workaround!==0){
                return new ValidationError("Required","Pflichtfeld");
            }
        }
        if (!this.validationFunction){
            return null;
        }
        return this.validationFunction(value);
    }

    nullable(): boolean{
        return this.isNullable;
    }

    getType(): AttributeType {
        return this.attributeType;
    }


    getPossibleEnumValues(): string[] {
        return this.possibleEnumValues;
    }
}
