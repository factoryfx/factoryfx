import { HttpUtility } from "./HttpUtility";
export class AttributeEditorEncryptedStringAttribute {
    constructor(attributeAccessor, inputId, factoryEditor, waitAnimation) {
        this.attributeAccessor = attributeAccessor;
        this.inputId = inputId;
        this.factoryEditor = factoryEditor;
        this.waitAnimation = waitAnimation;
    }
    create() {
        let form = document.createElement("form");
        form.className = "form-inline";
        let labelValue = document.createElement("label");
        labelValue.textContent = "Value";
        form.appendChild(labelValue);
        let inputValue = document.createElement("input");
        inputValue.id = this.inputId.toString();
        inputValue.className = "form-control";
        inputValue.type = "text";
        form.appendChild(inputValue);
        let labelKey = document.createElement("label");
        labelKey.textContent = "Key";
        form.appendChild(labelKey);
        let inputKey = document.createElement("input");
        inputKey.id = this.inputId.toString();
        inputKey.className = "form-control";
        inputKey.type = "text";
        form.appendChild(inputKey);
        let encryptButton = document.createElement("button");
        encryptButton.type = "button";
        encryptButton.textContent = "encrypt";
        encryptButton.onclick = (e) => {
            let encryptAttributeRequestBody = {
                "text": inputValue.value,
                "key": inputKey.value,
                "factoryId": this.factoryEditor.getCurrentData().getId(),
                "attributeVariableName": this.attributeAccessor.getAttributeName(),
                "root": this.factoryEditor.getCurrentData().getRoot().mapToJsonFromRoot()
            };
            HttpUtility.post("encryptAttribute", encryptAttributeRequestBody, this.waitAnimation, (response) => {
            });
        };
        encryptButton.className = "btn btn-secondary";
        form.appendChild(encryptButton);
        let decrypttButton = document.createElement("button");
        decrypttButton.type = "button";
        decrypttButton.textContent = "decrypt";
        decrypttButton.onclick = (e) => {
            let encryptAttributeRequestBody = {
                "key": inputKey.value,
                "factoryId": this.factoryEditor.getCurrentData().getId(),
                "attributeVariableName": this.attributeAccessor.getAttributeName(),
                "root": this.factoryEditor.getCurrentData().getRoot().mapToJsonFromRoot()
            };
            HttpUtility.post("decryptAttribute", encryptAttributeRequestBody, this.waitAnimation, (response) => {
                inputValue.value = response.text;
            });
        };
        decrypttButton.className = "btn btn-secondary";
        form.appendChild(decrypttButton);
        //input.required=!this.attributeAccessor.getAttributeMetadata().nullable();
        return form;
    }
}
