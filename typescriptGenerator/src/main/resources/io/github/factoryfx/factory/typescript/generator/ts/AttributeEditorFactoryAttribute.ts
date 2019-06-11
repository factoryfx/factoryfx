import {AttributeAccessor} from "./AttributeAccessor";
import {AttributeEditor} from "./AttributeEditor";
import {FactoryEditor} from "./FactoryEditor";

export class AttributeEditorFactoryAttribute implements AttributeEditor{

    constructor(private attributeAccessor: AttributeAccessor<any,any>, private inputId: string, private factoryEditor: FactoryEditor ) {

    }

    create(): HTMLElement{
        let value = this.attributeAccessor.getValue();

        let formGroup: HTMLElement= document.createElement("div");
        formGroup.setAttribute("class","form-group");

        let inputGroup: HTMLElement= document.createElement("div");
        inputGroup.setAttribute("class","input-group");

        let label: HTMLLabelElement= document.createElement("label");
        label.textContent=this.attributeAccessor.getLabelText('en')
        label.setAttribute("for",this.inputId.toString());

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

        formGroup.appendChild(label);
        formGroup.appendChild(inputGroup);
        return formGroup;
    }

}