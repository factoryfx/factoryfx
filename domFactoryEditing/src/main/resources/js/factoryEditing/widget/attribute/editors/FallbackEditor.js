import { AttributeEditorWidget } from "../AttributeEditorWidget";
export class FallbackEditor extends AttributeEditorWidget {
    constructor(attributeAccessor, inputId) {
        super(attributeAccessor, inputId);
        this.attributeAccessor = attributeAccessor;
        this.inputId = inputId;
    }
    render() {
        let div = document.createElement("div");
        div.textContent = 'not editable: ' + this.attributeAccessor.getAttributeMetadata().getType();
        return div;
    }
}
