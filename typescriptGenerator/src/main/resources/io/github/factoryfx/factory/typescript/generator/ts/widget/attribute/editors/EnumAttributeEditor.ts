import {AttributeAccessor} from "../../../AttributeAccessor";
import {AttributeEditorWidget} from "../AttributeEditorWidget";

export class EnumAttributeEditor extends AttributeEditorWidget{

    constructor(protected attributeAccessor: AttributeAccessor<any>, protected inputId: string) {
        super(attributeAccessor,inputId);
    }

    protected renderAttribute(): HTMLElement{
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
        option.value='';
        option.textContent='empty';
        if (value===null || value===undefined){
            option.selected=true;
        }
        select.options.add(option);

        return select;
    }



}