import {Data} from "./Data";
import {ValidationError} from "./ValidationError";
import {AttributeAccessor} from "./AttributeAccessor";
import {AttributeEditor} from "./AttributeEditor";

export class AttributeEditorByteAttribute implements AttributeEditor{

    constructor(private attributeAccessor: AttributeAccessor<any>, private inputId: string) {

    }

    create(): HTMLElement{
        let input: HTMLInputElement= document.createElement("input");
        input.id=this.inputId.toString();
        input.className="form-control";
        input.type="number";
        input.step='any';
        input.max="256";
        input.min="0";

        input.valueAsNumber=this.attributeAccessor.getValue();
        input.oninput=(e) => {
            this.attributeAccessor.setValue(input.valueAsNumber);
        };

        return input;
    }



}