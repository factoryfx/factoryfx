import {AttributeAccessor} from "../../../AttributeAccessor";
import {AttributeEditorWidget} from "../AttributeEditorWidget";

export class EnumListAttributeEditor extends AttributeEditorWidget{

    constructor(protected attributeAccessor: AttributeAccessor<any>, protected inputId: string) {
        super(attributeAccessor,inputId);
    }

    protected renderAttribute(): HTMLElement{
        let select: HTMLSelectElement= document.createElement("select");
        select.id=this.inputId.toString();
        select.className="form-control";
        select.multiple=true;


        let values: string[] = this.attributeAccessor.getValue();

        let onchangeEvent = (e: Event)=>{
            let collection: HTMLCollectionOf<HTMLOptionElement>= select.selectedOptions;
            let selectedValues=[];
            for (let i=0; i<collection.length; i++) {
                selectedValues.push(collection[i].value);
            }
            this.attributeAccessor.setValue(selectedValues);
        };
        select.onchange= onchangeEvent;

        for (let possibleValue of this.attributeAccessor.getAttributeMetadata().getPossibleEnumValues()){
            let option: HTMLOptionElement = document.createElement("option");
            option.value=possibleValue;
            if (values.includes(possibleValue)){
                option.selected=true;
            }
            option.onmousedown=(e)=>{
                e.preventDefault();
                option.selected=!option.selected;
                this.updateText(option,possibleValue);
                onchangeEvent(e);
                return false;
            };
            this.updateText(option,possibleValue);

            select.options.add(option)

        }
        return select;
    }

    updateText(option: HTMLOptionElement, possibleValue: string): void{
        if (option.selected){
            option.textContent= "\u2611 "+possibleValue;
        } else {
            option.textContent= "\u2610 "+possibleValue;
        }

    }



}