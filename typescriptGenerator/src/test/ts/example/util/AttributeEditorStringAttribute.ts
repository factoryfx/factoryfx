//generated code don't edit manually
import {Data} from "./Data";
import {ValidationError} from "./ValidationError";
import {AttributeAccessor} from "./AttributeAccessor";
import {AttributeEditor} from "./AttributeEditor";

export class AttributeEditorStringAttribute implements AttributeEditor{

    constructor(private attributeAccessor: AttributeAccessor<any,any>, private inputId: string) {

    }

    create(): HTMLElement{
        let div: HTMLElement= document.createElement("div");
        div.setAttribute("class","form-group");

        let label: HTMLLabelElement= document.createElement("label");
        label.setAttribute("for",this.inputId.toString());
        let input: HTMLInputElement= document.createElement("input");
        input.setAttribute("id",this.inputId.toString());
        input.setAttribute("class","form-control");
        label.textContent=this.attributeAccessor.getLabelText('en');

        div.appendChild(label);
        div.appendChild(input);

        input.value=this.attributeAccessor.getValue();
        input.addEventListener('input', (e) => {
            this.attributeAccessor.setValue(input.value);
        }, false);
        return div;
    }

}