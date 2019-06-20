export class AttributeEditorIntegerAttribute {
    constructor(attributeAccessor, inputId) {
        this.attributeAccessor = attributeAccessor;
        this.inputId = inputId;
    }
    create() {
        let input = document.createElement("input");
        input.id = this.inputId.toString();
        input.className = "form-control";
        input.type = "number";
        let value = this.attributeAccessor.getValue();
        if (value !== null && value !== undefined) {
            input.valueAsNumber = value;
        }
        input.oninput = (e) => {
            this.attributeAccessor.setValue(input.valueAsNumber);
        };
        input.required = !this.attributeAccessor.getAttributeMetadata().nullable();
        return input;
    }
}
