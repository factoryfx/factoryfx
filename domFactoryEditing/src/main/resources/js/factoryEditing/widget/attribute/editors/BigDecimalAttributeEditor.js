import { NumberBaseAttributeEditor } from "./NumberBaseAttributeEditor";
export class BigDecimalAttributeEditor extends NumberBaseAttributeEditor {
    constructor(attributeAccessor, inputId) {
        super(attributeAccessor, inputId);
        this.attributeAccessor = attributeAccessor;
        this.inputId = inputId;
    }
    additionalInputSetup() {
        this.input.type = "text";
    }
    bindModel() {
        this.renderOnce();
        this.input.value = this.attributeAccessor.getValue();
        this.input.oninput = (e) => {
            if (/^[+-]?(?=.)(?: \d+,)*\d*(?:\.\d+)?$/.test(this.input.value)) {
                this.attributeAccessor.setValue(this.input.value);
                this.input.setCustomValidity("");
            }
            else {
                this.input.setCustomValidity("Not a Number");
            }
        };
        this.input.required = !this.attributeAccessor.getAttributeMetadata().nullable();
    }
}
