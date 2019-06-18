//generated code don't edit manually
import {Data} from "./Data";
import {ValidationError} from "./ValidationError";
import {AttributeAccessor} from "./AttributeAccessor";
import {AttributeEditor} from "./AttributeEditor";

export class AttributeEditorIntegerAttribute implements AttributeEditor{

    constructor(private attributeAccessor: AttributeAccessor<any>, private inputId: string) {

    }

    create(): HTMLElement{
        let input: HTMLInputElement= document.createElement("input");
        input.id=this.inputId.toString();
        input.className="form-control";
        input.type="number";

        input.value=this.attributeAccessor.getValue();
        input.oninput= (e) => {
            console.log(input.value)
            this.attributeAccessor.setValue(input.value);
        };
        return input;
    }

}