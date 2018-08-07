import ValidationError from "./ValidationError";

export default abstract class Attribute<T>  {
    private readonly  en: string;
    private readonly  de: string;

    private isnullable: boolean= false;


    public constructor(en: string, de: string) {
        this.en = en;
        this.de = de;
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
        if (!this.isnullable && !value){
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
        this.isnullable=true;
    }

}
