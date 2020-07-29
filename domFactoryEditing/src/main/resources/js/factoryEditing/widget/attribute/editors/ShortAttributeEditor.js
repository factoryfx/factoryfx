import { NumberBaseAttributeEditor } from "./NumberBaseAttributeEditor";
export class ShortAttributeEditor extends NumberBaseAttributeEditor {
    constructor(attributeAccessor, inputId) {
        super(attributeAccessor, inputId);
        this.attributeAccessor = attributeAccessor;
        this.inputId = inputId;
    }
    additionalInputSetup() {
        this.input.max = "32767";
        this.input.min = "-32767";
    }
}
