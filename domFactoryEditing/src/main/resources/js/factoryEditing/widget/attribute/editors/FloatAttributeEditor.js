//generated code don't edit manually
import { AttributeEditorWidget } from "../AttributeEditorWidget";
export class FloatAttributeEditor extends AttributeEditorWidget {
    constructor(attributeAccessor, inputId) {
        super(attributeAccessor, inputId);
        this.attributeAccessor = attributeAccessor;
        this.inputId = inputId;
    }
    renderAttribute() {
        let input = document.createElement("input");
        input.id = this.inputId.toString();
        input.className = "form-control";
        input.type = "number";
        input.step = 'any';
        input.valueAsNumber = this.attributeAccessor.getValue();
        input.oninput = (e) => {
            this.attributeAccessor.setValue(input.valueAsNumber);
        };
        return input;
    }
}
