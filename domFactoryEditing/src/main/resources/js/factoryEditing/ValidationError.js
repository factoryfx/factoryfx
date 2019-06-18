//generated code don't edit manually
export class ValidationError {
    constructor(enErrorText, deErrorText) {
        this.enErrorText = enErrorText;
        this.deErrorText = deErrorText;
    }
    getDisplayText(locale) {
        if (locale === "de") {
            return this.deErrorText;
        }
        else {
            return this.enErrorText;
        }
    }
}
