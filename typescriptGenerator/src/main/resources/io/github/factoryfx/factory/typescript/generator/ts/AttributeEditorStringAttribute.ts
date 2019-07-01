import {AttributeAccessor} from "./AttributeAccessor";
import {AttributeEditor} from "./AttributeEditor";

export class AttributeEditorStringAttribute implements AttributeEditor{

    constructor(private attributeAccessor: AttributeAccessor<any>, private inputId: string) {

    }

    create(): HTMLElement{
        let input: HTMLInputElement= document.createElement("input");
        input.id=this.inputId.toString();
        input.className="form-control";
        input.type="text";

        input.value=this.attributeAccessor.getValue();
        input.oninput=(e) => {
            this.attributeAccessor.setValue(input.value);
        };

        input.required=!this.attributeAccessor.getAttributeMetadata().nullable();
        return input;
    }



}