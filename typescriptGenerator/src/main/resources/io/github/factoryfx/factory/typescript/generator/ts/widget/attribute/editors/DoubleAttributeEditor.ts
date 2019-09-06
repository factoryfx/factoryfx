import {AttributeAccessor} from "../../../AttributeAccessor";
import {AttributeEditorWidget} from "../AttributeEditorWidget";

export class DoubleAttributeEditor extends AttributeEditorWidget{

    constructor(protected attributeAccessor: AttributeAccessor<any>, protected inputId: string) {
        super(attributeAccessor,inputId);
    }

    protected renderAttribute(): HTMLElement{
        let input: HTMLInputElement= document.createElement("input");
        input.id=this.inputId.toString();
        input.className="form-control";
        input.type="number";
        input.step='any';

        input.valueAsNumber=this.attributeAccessor.getValue();
        input.oninput=(e) => {
            this.attributeAccessor.setValue(input.valueAsNumber);
        };

        return input;
    }



}