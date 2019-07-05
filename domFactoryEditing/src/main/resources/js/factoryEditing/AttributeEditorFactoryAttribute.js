import { HttpUtility } from "./HttpUtility";
export class AttributeEditorFactoryAttribute {
    constructor(attributeAccessor, inputId, factoryEditor, waitAnimation) {
        this.attributeAccessor = attributeAccessor;
        this.inputId = inputId;
        this.factoryEditor = factoryEditor;
        this.waitAnimation = waitAnimation;
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
    create() {
        let inputGroup = document.createElement("div");
        inputGroup.setAttribute("class", "input-group");
        this.bindValue();
        let inputGroupAppend = document.createElement("div");
        inputGroupAppend.className = "input-group-append";
        let removeButton = document.createElement("button");
        removeButton.type = "button";
        removeButton.textContent = "remove";
        removeButton.onclick = (e) => {
            this.attributeAccessor.setValue(null);
            this.bindValue();
            this.factoryEditor.updateTree();
        };
        removeButton.disabled = !this.attributeAccessor.getAttributeMetadata().nullable();
        removeButton.className = "btn btn-outline-danger";
        let newButton = document.createElement("button");
        newButton.type = "button";
        newButton.textContent = "new";
        newButton.onclick = (e) => {
            let createRequestBody = {
                "factoryId": this.factoryEditor.getCurrentData().getId(),
                "attributeVariableName": this.attributeAccessor.getAttributeName(),
                "root": this.factoryEditor.getCurrentData().getRoot().mapToJsonFromRoot()
            };
            HttpUtility.post("createNewFactory", createRequestBody, this.waitAnimation, (response) => {
                this.attributeAccessor.setValue(this.factoryEditor.getCurrentData().createNewChildFactory(response));
                this.bindValue();
                this.factoryEditor.updateTree();
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
            if (this.factoryEditor.validate()) {
                this.factoryEditor.edit(value);
            }
        };
        if (!value) {
            this.editButton.disabled = true;
        }
        this.input.ondblclick = undefined;
        if (value) {
            this.input.ondblclick = (e) => {
                if (this.factoryEditor.validate()) {
                    this.factoryEditor.edit(value);
                }
            };
        }
    }
}
