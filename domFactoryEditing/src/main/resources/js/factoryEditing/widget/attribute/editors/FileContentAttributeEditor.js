import { AttributeEditorWidget } from "../AttributeEditorWidget";
export class FileContentAttributeEditor extends AttributeEditorWidget {
    constructor(attributeAccessor, inputId) {
        super(attributeAccessor, inputId);
        this.attributeAccessor = attributeAccessor;
        this.inputId = inputId;
        this.textarea = document.createElement("textarea");
        this.textarea.className = "form-control";
    }
    renderAttribute() {
        let content = document.createElement("div");
        let input = document.createElement("input");
        input.id = this.inputId.toString();
        input.className = "form-control-file";
        input.type = "file";
        // input.value=this.attributeAccessor.getValue();  TODO how show base 64
        input.oninput = (e) => {
            if (input.files) {
                let reader = new FileReader();
                reader.readAsDataURL(input.files[0]);
                reader.onload = () => {
                    let result = reader.result;
                    this.attributeAccessor.setValue(result.split(',')[1]);
                    this.bindValue();
                };
            }
        };
        this.bindValue();
        content.appendChild(this.textarea);
        content.appendChild(input);
        return content;
    }
    bindValue() {
        let value = this.attributeAccessor.getValue();
        this.textarea.value = value;
        // this.textarea.readOnly=true; doesn't work width required
        this.textarea.required = !this.attributeAccessor.getAttributeMetadata().nullable();
    }
}
