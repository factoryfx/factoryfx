export class AttributeEditorByteAttribute {
    constructor(attributeAccessor, inputId) {
        this.attributeAccessor = attributeAccessor;
        this.inputId = inputId;
    }
    create() {
        let input = document.createElement("input");
        input.id = this.inputId.toString();
        input.className = "form-control";
        input.type = "number";
        input.step = 'any';
        input.max = "256";
        input.min = "0";
        input.valueAsNumber = this.attributeAccessor.getValue();
        input.oninput = (e) => {
            this.attributeAccessor.setValue(input.valueAsNumber);
        };
        return input;
    }
}
