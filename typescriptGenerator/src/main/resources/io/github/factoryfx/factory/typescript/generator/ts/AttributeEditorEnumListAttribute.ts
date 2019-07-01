import {Data} from "./Data";
import {ValidationError} from "./ValidationError";
import {AttributeAccessor} from "./AttributeAccessor";
import {AttributeEditor} from "./AttributeEditor";

export class AttributeEditorEnumListAttribute implements AttributeEditor{

    constructor(private attributeAccessor: AttributeAccessor<any>, private inputId: string) {

    }

    create(): HTMLElement{
        let select: HTMLSelectElement= document.createElement("select");
        select.id=this.inputId.toString();
        select.className="form-control";
        select.multiple=true;


        let values: string[] = this.attributeAccessor.getValue();

        select.onchange=(e)=>{
            let collection: HTMLCollectionOf<HTMLOptionElement>= select.selectedOptions;
            let selectedValues=[];
            for (let i=0; i<collection.length; i++) {
                selectedValues.push(collection[i].value);
            }
            this.attributeAccessor.setValue(selectedValues);

        };

        for (let possibleValue of this.attributeAccessor.getAttributeMetadata().getPossibleEnumValues()){
            let option: HTMLOptionElement = document.createElement("option");
            option.textContent=possibleValue;
            option.value=possibleValue;
            if (values.includes(possibleValue)){
                option.selected=true;
            }
            select.options.add(option)

        }
        return select;
    }



}