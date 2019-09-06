//generated code don't edit manually
import { AttributeEditorWidget } from "../AttributeEditorWidget";
export class FactoryAttributeEditor extends AttributeEditorWidget {
    constructor(attributeAccessor, inputId, factoryEditorNode, httpClient) {
        super(attributeAccessor, inputId);
        this.attributeAccessor = attributeAccessor;
        this.inputId = inputId;
        this.factoryEditorNode = factoryEditorNode;
        this.httpClient = httpClient;
        this.editButton = document.createElement("button");
        this.input = document.createElement("input");
        this.editButton.type = "button";
        this.editButton.textContent = "edit";
        this.editButton.className = "btn btn-outline-secondary";
        this.input.id = this.inputId.toString();
        this.input.className = "form-control";
        this.input.readOnly = true;
        this.input.required = !this.attributeAccessor.getAttributeMetadata().nullable();
    }
    renderAttribute() {
        let inputGroup = document.createElement("div");
        inputGroup.className = "input-group";
        this.bindValue();
        let inputGroupAppend = document.createElement("div");
        inputGroupAppend.className = "input-group-append";
        let removeButton = document.createElement("button");
        removeButton.type = "button";
        removeButton.textContent = "remove";
        removeButton.onclick = (e) => {
            this.attributeAccessor.setValue(null);
        };
        removeButton.disabled = !this.attributeAccessor.getAttributeMetadata().nullable();
        removeButton.className = "btn btn-outline-danger";
        let newButton = document.createElement("button");
        newButton.type = "button";
        newButton.textContent = "new";
        newButton.onclick = (e) => {
            this.httpClient.createNewFactory(this.factoryEditorNode.getFactory().getId(), this.attributeAccessor.getAttributeName(), this.factoryEditorNode.getFactory().getRoot(), (response) => {
                this.attributeAccessor.setValue(this.factoryEditorNode.getFactory().createNewChildFactory(response));
            });
        };
        newButton.className = "btn btn-outline-secondary";
        inputGroupAppend.appendChild(newButton);
        inputGroupAppend.appendChild(removeButton);
        inputGroupAppend.appendChild(this.editButton);
        inputGroup.appendChild(this.input);
        inputGroup.appendChild(inputGroupAppend);
        return inputGroup;
    }
    bindValue() {
        let value = this.attributeAccessor.getValue();
        if (value) {
            this.input.value = value.getDisplayText();
        }
        else {
            this.input.value = '';
        }
        this.editButton.onclick = (e) => {
            if (this.factoryEditorNode.getWidget().validate()) {
                this.factoryEditorNode.edit(value);
            }
        };
        if (!value) {
            this.editButton.disabled = true;
        }
        this.input.ondblclick = null;
        if (value) {
            this.input.ondblclick = (e) => {
                if (this.factoryEditorNode.getWidget().validate()) {
                    this.factoryEditorNode.edit(value);
                }
            };
        }
    }
}
