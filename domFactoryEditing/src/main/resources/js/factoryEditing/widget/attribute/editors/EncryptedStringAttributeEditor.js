import { AttributeEditorWidget } from "../AttributeEditorWidget";
import { BootstrapUtility } from "../../../BootstrapUtility";
export class EncryptedStringAttributeEditor extends AttributeEditorWidget {
    constructor(model, factoryEditorModel, httpClient) {
        super(model.attributeAccessor.get(), model.inputId.get());
        this.model = model;
        this.factoryEditorModel = factoryEditorModel;
        this.httpClient = httpClient;
        this.encytptedTextInput = document.createElement("input");
        this.decryptButton = document.createElement("button");
        this.inputKey = document.createElement("input");
        this.inputValue = document.createElement("input");
        this.encryptButton = document.createElement("button");
    }
    renderAttribute() {
        let form = document.createElement("form");
        form.className = "form-inline";
        this.inputKey.id = this.inputId.toString();
        this.inputKey.className = "form-control";
        this.inputKey.type = "text";
        this.inputKey.style.marginRight = "3px";
        this.inputKey.placeholder = "Key";
        form.appendChild(this.inputKey);
        let encytptedTextLabel = document.createElement("label");
        encytptedTextLabel.textContent = "Encrypted";
        encytptedTextLabel.style.marginRight = "3px";
        form.appendChild(encytptedTextLabel);
        this.encytptedTextInput.id = this.inputId.toString();
        this.encytptedTextInput.type = "text";
        this.encytptedTextInput.readOnly = true;
        this.decryptButton.textContent = "decrypt";
        let inputGroup1 = BootstrapUtility.createInputGroup(this.encytptedTextInput, this.decryptButton);
        inputGroup1.style.marginRight = "3px";
        form.appendChild(inputGroup1);
        let labelValue = document.createElement("label");
        labelValue.textContent = "Value";
        labelValue.style.marginRight = "3px";
        form.appendChild(labelValue);
        this.inputValue.id = this.inputId.toString();
        this.inputValue.type = "text";
        this.encryptButton.textContent = "encrypt";
        let inputGroup2 = BootstrapUtility.createInputGroup(this.inputValue, this.encryptButton);
        inputGroup2.style.marginRight = "3px";
        form.appendChild(inputGroup2);
        //input.required=!this.attributeAccessor.getAttributeMetadata().nullable();
        return form;
    }
    bindAttribute() {
        this.encytptedTextInput.value = this.attributeAccessor.getValue().encryptedString;
        this.decryptButton.disabled = !(!!this.model.key.get());
        this.decryptButton.onclick = (e) => {
            this.httpClient.decryptAttribute(this.attributeAccessor.getValue().encryptedString, this.inputKey.value, (response) => {
                this.inputValue.value = response.text;
            });
        };
        if (this.model.key.get()) {
            this.inputKey.value = this.model.key.get();
        }
        this.inputKey.oninput = (e) => {
            this.model.key.set(this.inputKey.value);
        };
        this.encryptButton.disabled = !(!!this.model.key.get());
        this.encryptButton.onclick = (e) => {
            this.httpClient.encryptAttribute(this.inputValue.value, this.inputKey.value, (encryptedText) => {
                this.attributeAccessor.setValue({
                    encryptedString: encryptedText
                });
            });
        };
        this.inputValue.disabled = !(!!this.model.key.get());
    }
}
