import { AttributeEditorWidget } from "../AttributeEditorWidget";
export class StringAttributeEditor extends AttributeEditorWidget {
    constructor(attributeAccessor, inputId) {
        super(attributeAccessor, inputId);
        this.attributeAccessor = attributeAccessor;
        this.inputId = inputId;
        this.input = document.createElement("input");
    }
    renderAttribute() {
        this.input.id = this.inputId.toString();
        this.input.className = "form-control";
        this.input.type = "text";
        this.input.required = !this.attributeAccessor.getAttributeMetadata().nullable();
        return this.input;
    }
    bindAttribute() {
        this.input.value = this.attributeAccessor.getValue();
        this.input.oninput = (e) => {
            this.attributeAccessor.setValue(this.input.value);
        };
        this.input.required = !this.attributeAccessor.getAttributeMetadata().nullable();
    }
}
