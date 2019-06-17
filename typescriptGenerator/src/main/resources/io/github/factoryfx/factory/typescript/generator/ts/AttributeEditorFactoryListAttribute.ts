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
        let div: HTMLElement= document.createElement("div");

        let ul: HTMLElement= document.createElement("ul");
        ul.setAttribute("class","list-group");

        for (let value of values) {
            let li: HTMLElement= document.createElement("li");
            li.setAttribute("class","list-group-item");
            li.appendChild(this.createListItem(value));
            ul.appendChild(li);
        }

        div.appendChild(ul);
        return div;
    }

    createListItem(value: Data): HTMLElement{
        let inputGroup: HTMLElement= document.createElement("div");
        inputGroup.setAttribute("class","input-group");

        let input: HTMLInputElement= document.createElement("input");
        input.setAttribute("id",this.inputId.toString());
        input.setAttribute("class","form-control");
        input.setAttribute("id",this.inputId);
        input.setAttribute("readonly","readonly");

        if (value){
            input.setAttribute("value",value.getDisplayText());
        } else {
            input.setAttribute("value",'');
        }

        let inputGroupAppend: HTMLElement= document.createElement("div");
        inputGroupAppend.setAttribute("class","input-group-append");

        let button: HTMLElement= document.createElement("button");
        button.textContent="edit";
        button.onclick=(e)=>{
            this.factoryEditor.edit(value);
        };
        if (!value){
            button.setAttribute("disabled","disabled");
        }
        button.setAttribute("class","btn btn-outline-secondary");

        inputGroupAppend.appendChild(button);
        inputGroup.appendChild(input);
        inputGroup.appendChild(inputGroupAppend);
        return inputGroup;
    }

}