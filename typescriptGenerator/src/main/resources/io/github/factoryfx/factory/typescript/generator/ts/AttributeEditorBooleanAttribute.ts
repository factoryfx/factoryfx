import {AttributeEditor} from "./AttributeEditor";
import {AttributeAccessor} from "./AttributeAccessor";

export class AttributeEditorBooleanAttribute implements AttributeEditor{

    constructor(private attributeAccessor: AttributeAccessor<any>, private inputId: string) {

    }

    create(): HTMLElement{
        let div: HTMLDivElement= document.createElement("div");
        div.className="form-check";

        let input: HTMLInputElement= document.createElement("input");
        input.id=this.inputId.toString();
        input.className="form-check-input position-static";
        input.type="checkbox";

        input.checked=this.attributeAccessor.getValue();
        input.oninput= (e) => {
            this.attributeAccessor.setValue(input.checked);
        };

        div.appendChild(input);
        return div;
    }

}