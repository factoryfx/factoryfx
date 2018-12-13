import { ValidationError } from "./ValidationError";
import {AttributeType} from "./AttributeType";

export class AttributeMetadata<T>  {
    private readonly  en: string;
    private readonly  de: string;

    private isNullable: boolean= false;

    private attributeType: AttributeType;


    public constructor(en: string, de: string, attributeType: AttributeType) {
        this.en = en;
        this.de = de;
        this.attributeType=attributeType;
    }

    public getLabelText(locale: string): string{
        if (locale==='de'){
            return 'de';
        }
        return 'en';
    }

    validationFunction:(root: T)=>ValidationError;
    validation(validationFunction:(root: T)=>ValidationError): void{
        this.validationFunction=validationFunction;
    }

    validate(value: T):ValidationError{
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

    nullable(){
        this.isNullable=true;
    }

    getType(): AttributeType {
        return this.attributeType;
    }



}
