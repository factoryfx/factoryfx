import {AttributeAccessor} from "./AttributeAccessor";
import {AttributeEditor} from "./AttributeEditor";
import {FactoryEditor} from "./FactoryEditor";

export class AttributeEditorFactoryAttribute implements AttributeEditor{

    private editButton: HTMLButtonElement= document.createElement("button");
    private input: HTMLInputElement= document.createElement("input");


    constructor(private attributeAccessor: AttributeAccessor<any>, private inputId: string, private factoryEditor: FactoryEditor ) {
        this.editButton.type="button";
        this.editButton.textContent="edit";
        this.editButton.className="btn btn-outline-secondary";

        this.input.id=this.inputId.toString();
        this.input.className="form-control";
        this.input.readOnly=true;
    }

    create(): HTMLElement{
        let inputGroup: HTMLElement= document.createElement("div");
        inputGroup.setAttribute("class","input-group");

        this.bindValue();

        let inputGroupAppend: HTMLElement= document.createElement("div");
        inputGroupAppend.className="input-group-append";

        let removeButton: HTMLButtonElement= document.createElement("button");
        removeButton.type="button";
        removeButton.textContent="remove";
        removeButton.onclick=(e)=>{
            this.attributeAccessor.setValue(null);
            this.bindValue();
        };
        removeButton.disabled=!this.attributeAccessor.getAttributeMetadata().nullable();
        removeButton.className="btn btn-outline-secondary";



        inputGroupAppend.appendChild(removeButton);
        inputGroupAppend.appendChild(this.editButton);
        inputGroup.appendChild(this.input);
        inputGroup.appendChild(inputGroupAppend);

        return inputGroup;
    }

    private bindValue() {
        let value = this.attributeAccessor.getValue();
        if (value) {
            this.input.value = value.getDisplayText();
        } else {
            this.input.value = '';
        }
        this.editButton.onclick = (e) => {
            this.factoryEditor.edit(value);
        };
        if (!value) {
            this.editButton.disabled = true;
        }

        this.input.ondblclick=undefined;
        if (value) {
            this.input.ondblclick = (e) => {
                this.factoryEditor.edit(value);
            }
        }
    }

}