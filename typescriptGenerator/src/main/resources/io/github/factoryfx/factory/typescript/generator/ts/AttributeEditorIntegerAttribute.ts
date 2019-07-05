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
        input.max="2147483647";
        input.min="-2147483648";

        let value = this.attributeAccessor.getValue();
        if (value!==null && value!==undefined){
            input.valueAsNumber= value;
        }
        input.oninput= (e) => {
            this.attributeAccessor.setValue(input.valueAsNumber);
        };
        input.required=!this.attributeAccessor.getAttributeMetadata().nullable();
        return input;
    }

}