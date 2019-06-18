export class AttributeEditorFallback {
    constructor(attributeAccessor, inputId) {
        this.attributeAccessor = attributeAccessor;
        this.inputId = inputId;
    }
    create() {
        let div = document.createElement("div");
        div.textContent = 'not editable: ' + this.attributeAccessor.getAttributeMetadata().getType();
        return div;
    }
}
