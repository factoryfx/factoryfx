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


        let input: HTMLInputElement= document.createElement("input");
        input.setAttribute("id",this.inputId.toString());
        input.setAttribute("class","form-control");

        div.appendChild(input);

        input.value=this.attributeAccessor.getValue();
        input.addEventListener('input', (e) => {
            this.attributeAccessor.setValue(input.value);
        }, false);
        return div;
    }

}