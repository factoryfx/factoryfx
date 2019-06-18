//generated code don't edit manually
import {AttributeAccessor} from "./AttributeAccessor";
import {AttributeEditor} from "./AttributeEditor";
import {FactoryEditor} from "./FactoryEditor";
import {Data} from "./Data";

export class AttributeEditorFactoryListAttribute implements AttributeEditor{

    constructor(private attributeAccessor: AttributeAccessor<any>, private inputId: string, private factoryEditor: FactoryEditor ) {

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
        input.id=this.inputId.toString();
        input.className="form-control";
        input.readOnly=true;

        input.ondblclick=(e)=>{
            this.factoryEditor.edit(value);
        };

        if (value){
            input.value=value.getDisplayText();
        } else {
            input.value='';
        }

        let inputGroupAppend: HTMLElement= document.createElement("div");
        inputGroupAppend.className="input-group-append";

        let button: HTMLButtonElement= document.createElement("button");
        button.textContent="edit";
        button.onclick=(e)=>{
            this.factoryEditor.edit(value);
        };
        if (!value){
            button.disabled=true;
        }
        button.className="btn btn-outline-secondary";

        inputGroupAppend.appendChild(button);
        inputGroup.appendChild(input);
        inputGroup.appendChild(inputGroupAppend);
        return inputGroup;
    }

}