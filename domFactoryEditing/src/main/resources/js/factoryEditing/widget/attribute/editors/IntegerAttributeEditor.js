import { NumberBaseAttributeEditor } from "./NumberBaseAttributeEditor";
export class IntegerAttributeEditor extends NumberBaseAttributeEditor {
    constructor(attributeAccessor, inputId) {
        super(attributeAccessor, inputId);
        this.attributeAccessor = attributeAccessor;
        this.inputId = inputId;
    }
    additionalInputSetup() {
        this.input.max = "2147483647";
        this.input.min = "-2147483648";
    }
}
