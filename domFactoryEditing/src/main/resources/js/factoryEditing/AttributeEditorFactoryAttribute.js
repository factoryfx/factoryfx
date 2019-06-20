export class AttributeEditorFactoryAttribute {
    constructor(attributeAccessor, inputId, factoryEditor) {
        this.attributeAccessor = attributeAccessor;
        this.inputId = inputId;
        this.factoryEditor = factoryEditor;
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
        };
        removeButton.disabled = !this.attributeAccessor.getAttributeMetadata().nullable();
        removeButton.className = "btn btn-outline-secondary";
        let newButton = document.createElement("button");
        newButton.type = "button";
        newButton.textContent = "new";
        newButton.onclick = (e) => {
            let createRequest = new XMLHttpRequest();
            createRequest.open("POST", "updateCurrentFactory");
            createRequest.setRequestHeader("Content-type", "application/json");
            let createRequestBody = {
                "javaClass": "io.github.factoryfx.dom.rest.MicroserviceDomResourceTest$JettyServerRootFactory",
                "attributeVariableName": "handler"
            };
            createRequest.onload = (e) => {
                let response = JSON.parse(createRequest.responseText);
                this.attributeAccessor.setValue(null);
                this.bindValue();
            };
            createRequest.send(JSON.stringify(createRequestBody));
        };
        newButton.className = "btn btn-outline-secondary";
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
