import {AttributeAccessor} from "./AttributeAccessor";
import {AttributeEditor} from "./AttributeEditor";
import {FactoryEditor} from "./FactoryEditor";
import {Data} from "./Data";

export class AttributeEditorFactoryListAttribute implements AttributeEditor{

    constructor(private attributeAccessor: AttributeAccessor<any,any>, private inputId: string, private factoryEditor: FactoryEditor ) {

    }

    create(): HTMLElement{
        let values: Data[] = this.attributeAccessor.getValue();
        if (!values){
            values=[];
        }
        let formGroup: HTMLElement= document.createElement("div");
        formGroup.setAttribute("class","form-group");

        let ul: HTMLElement= document.createElement("ul");
        ul.setAttribute("class","list-group");

        for (let value of values) {
            let li: HTMLElement= document.createElement("li");
            li.setAttribute("class","list-group-item");
            li.textContent=value.getDisplayText();
            ul.appendChild(li);
        }

        formGroup.appendChild(ul);
        return formGroup;
    }

}