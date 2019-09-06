import {DomUtility} from "../../../DomUtility";
import {Data} from "../../../Data";
import {AttributeAccessor} from "../../../AttributeAccessor";
import {AttributeEditorWidget} from "../AttributeEditorWidget";
import {FactoryEditorModel} from "../../factoryeditor/FactoryEditorModel";
import {HttpClient} from "../../../HttpClient";

export class FactoryListAttributeEditor extends AttributeEditorWidget{
    private div: HTMLElement= document.createElement("div");
    constructor(protected attributeAccessor: AttributeAccessor<any>, protected inputId: string, private factoryEditorModel: FactoryEditorModel, private httpClient: HttpClient) {
        super(attributeAccessor,inputId);
    }

    protected renderAttribute(): HTMLElement{
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
        ul.style.marginBottom="16px";
        ul.hidden = values.length===0

        let newButton: HTMLButtonElement = document.createElement("button");
        newButton.type="button";
        newButton.textContent="add";
        newButton.onclick=(e)=>{

            this.httpClient.createNewFactory(
                this.factoryEditorModel.getFactory().getId(),
                this.attributeAccessor.getAttributeName(),
                this.factoryEditorModel.getFactory().getRoot(),(response)=>{
                    this.attributeAccessor.getValue().push(this.factoryEditorModel.getFactory().createNewChildFactory(response));
                    this.update();
                    // this.factoryEditor.updateTree();
                }
            )

        };
        newButton.className="btn btn-primary";

        this.div.appendChild(ul);
        this.div.appendChild(newButton);
    }

    createListItem(value: Data): HTMLElement{
        let inputGroup: HTMLElement= document.createElement("div");
        inputGroup.setAttribute("class","input-group");

        let input: HTMLInputElement= document.createElement("input");
        input.id=this.inputId.toString();
        input.className="form-control";
        input.readOnly=true;

        input.ondblclick=(e)=>{
            this.factoryEditorModel.edit(value)
        };

        if (value){
            input.value=value.getDisplayText();
        } else {
            input.value='';
        }

        let inputGroupAppend: HTMLElement= document.createElement("div");
        inputGroupAppend.className="input-group-append";

        let removeButton: HTMLButtonElement= document.createElement("button");
        removeButton.type="button";
        removeButton.textContent="remove";
        removeButton.onclick=(e)=>{
            let array: Data[] = this.attributeAccessor.getValue();
            array.splice(array.indexOf(value), 1);
            this.update();
            // this.factoryEditor.updateTree();

        };
        removeButton.className="btn btn-outline-danger";

        let editButton: HTMLButtonElement= document.createElement("button");
        editButton.textContent="edit";
        editButton.onclick=(e)=>{
            // this.factoryEditor.edit(value);
            this.factoryEditorModel.edit(value)
        };
        if (!value){
            editButton.disabled=true;
        }
        editButton.className="btn btn-outline-secondary";

        inputGroupAppend.appendChild(removeButton);
        inputGroupAppend.appendChild(editButton);
        inputGroup.appendChild(input);
        inputGroup.appendChild(inputGroupAppend);
        return inputGroup;
    }

}