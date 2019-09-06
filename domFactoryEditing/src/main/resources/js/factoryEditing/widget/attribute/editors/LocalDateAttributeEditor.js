import { AttributeEditorWidget } from "../AttributeEditorWidget";
export class LocalDateAttributeEditor extends AttributeEditorWidget {
    constructor(attributeAccessor, inputId) {
        super(attributeAccessor, inputId);
        this.attributeAccessor = attributeAccessor;
        this.inputId = inputId;
    }
    renderAttribute() {
        let input = document.createElement("input");
        input.id = this.inputId.toString();
        input.className = "form-control";
        input.type = "date";
        input.value = this.attributeAccessor.getValue();
        input.oninput = (e) => {
            this.attributeAccessor.setValue(input.valueAsDate);
        };
        input.required = !this.attributeAccessor.getAttributeMetadata().nullable();
        return input;
    }
}
