import {AttributeAccessor} from "./AttributeAccessor";
import {AttributeEditor} from "./AttributeEditor";
import {FactoryEditor} from "./FactoryEditor";
import {Data} from "./Data";
import {HttpUtility} from "./HttpUtility";
import {WaitAnimation} from "./WaitAnimation";
import {DomUtility} from "./DomUtility";

export class AttributeEditorFactoryListAttribute implements AttributeEditor{
    private div: HTMLElement= document.createElement("div");
    constructor(private attributeAccessor: AttributeAccessor<any>, private inputId: string, private factoryEditor: FactoryEditor, private waitAnimation: WaitAnimation) {

    }

    create(): HTMLElement{
        this.update();
        return this.div;
    }

    public update(){
        DomUtility.clear(this.div);
        let values: Data[] = this.attributeAccessor.getValue();
        if (!values){
            values=[];
        }


        let ul: HTMLElement= document.createElement("ul");
        ul.setAttribute("class","list-group");

        for (let value of values) {
            let li: HTMLElement= document.createElement("li");
            li.setAttribute("class","list-group-item");
            li.appendChild(this.createListItem(value));
            ul.appendChild(li);
        }

        let newButton: HTMLButtonElement = document.createElement("button");
        newButton.type="button";
        newButton.textContent="add";
        newButton.onclick=(e)=>{
            let createRequestBody = {
                "factoryId" : this.factoryEditor.getCurrentData().getId(),
                "attributeVariableName" : this.attributeAccessor.getAttributeName(),
                "root" : this.factoryEditor.getCurrentData().getRoot().mapToJsonFromRoot()
            };
            HttpUtility.post("createNewFactory",createRequestBody,this.waitAnimation,(response)=>{
                this.attributeAccessor.getValue().push(this.factoryEditor.getCurrentData().createNewChildFactory(response));
                this.update();
            });
        };
        newButton.className="btn btn-primary";

        this.div.appendChild(ul);
        this.div.appendChild(newButton);
    }


    public createList(values: Data[]): HTMLElement{
        let ul: HTMLElement= document.createElement("ul");
        ul.setAttribute("class","list-group");

        for (let value of values) {
            let li: HTMLElement= document.createElement("li");
            li.setAttribute("class","list-group-item");
            li.appendChild(this.createListItem(value));
            ul.appendChild(li);
        }
        return ul;
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