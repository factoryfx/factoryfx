import { NumberBaseAttributeEditor } from "./NumberBaseAttributeEditor";
export class FloatAttributeEditor extends NumberBaseAttributeEditor {
    constructor(attributeAccessor, inputId) {
        super(attributeAccessor, inputId);
        this.attributeAccessor = attributeAccessor;
        this.inputId = inputId;
    }
    additionalInputSetup() {
        this.input.step = 'any';
    }
}
