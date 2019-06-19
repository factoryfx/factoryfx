export class AttributeEditorBooleanAttribute {
    constructor(attributeAccessor, inputId) {
        this.attributeAccessor = attributeAccessor;
        this.inputId = inputId;
    }
    create() {
        let div = document.createElement("div");
        div.className = "form-check";
        let input = document.createElement("input");
        input.id = this.inputId.toString();
        input.className = "form-check-input position-static";
        input.type = "checkbox";
        input.checked = this.attributeAccessor.getValue();
        input.oninput = (e) => {
            this.attributeAccessor.setValue(input.checked);
        };
        div.appendChild(input);
        return div;
    }
}
