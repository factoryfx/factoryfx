//generated code don't edit manually
export default class ValidationError {
    public enErrorText: string;
    public deErrorText: string;

    constructor(enErrorText: string, deErrorText: string){
        this.enErrorText = enErrorText;
        this.deErrorText = deErrorText;
    }

    getDisplayText(locale: string): string{
        if (locale==="de"){
            return this.deErrorText;
        } else {
            return this.enErrorText;
        }
    }
}
