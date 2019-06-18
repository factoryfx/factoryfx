import {Data} from "./Data";
import {ValidationError} from "./ValidationError";
import {AttributeAccessor} from "./AttributeAccessor";
import {AttributeEditor} from "./AttributeEditor";

export class AttributeEditorEnumAttribute implements AttributeEditor{

    constructor(private attributeAccessor: AttributeAccessor<any>, private inputId: string) {

    }

    create(): HTMLElement{
        let select: HTMLSelectElement= document.createElement("select");
        select.id=this.inputId.toString();
        select.className="form-control";


        let value = this.attributeAccessor.getValue();
        select.value= value;
        select.oninput=(e) => {
            this.attributeAccessor.setValue(select.value);
        };

        for (let possibleValue of this.attributeAccessor.getAttributeMetadata().getPossibleEnumValues()){
            let option: HTMLOptionElement = document.createElement("option");
            option.textContent=possibleValue;
            if (value===possibleValue){
                option.selected=true;
            }
            select.options.add(option)
        }

        let option: HTMLOptionElement = document.createElement("option");
        option.value=null;
        option.textContent='empty';
        if (value===null || value===undefined){
            option.selected=true;
        }
        select.options.add(option);

        return select;
    }



}