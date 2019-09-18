import { AttributeEditorWidget } from "../AttributeEditorWidget";
export class NumberBaseAttributeEditor extends AttributeEditorWidget {
    constructor(attributeAccessor, inputId) {
        super(attributeAccessor, inputId);
        this.attributeAccessor = attributeAccessor;
        this.inputId = inputId;
        this.input = document.createElement("input");
        this.input.id = this.inputId.toString();
        this.input.className = "form-control";
        this.input.type = "number";
        this.additionalInputSetup();
    }
    render() {
        return this.input;
    }
    bindModel() {
        this.renderOnce();
        this.input.valueAsNumber = this.attributeAccessor.getValue();
        this.input.oninput = (e) => {
            this.attributeAccessor.setValue(this.input.valueAsNumber);
        };
        this.input.required = !this.attributeAccessor.getAttributeMetadata().nullable();
    }
}
