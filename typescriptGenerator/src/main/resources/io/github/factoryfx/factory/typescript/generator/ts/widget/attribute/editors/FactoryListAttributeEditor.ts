import {Data} from "../../../Data";
import {AttributeAccessor} from "../../../AttributeAccessor";
import {AttributeEditorWidget} from "../AttributeEditorWidget";
import {FactoryEditorModel} from "../../factoryeditor/FactoryEditorModel";
import {HttpClient} from "../../../HttpClient";
import {BootstrapUtility} from "../../../BootstrapUtility";
import {FactorySelectDialog} from "../../../utility/FactorySelectDialog";
import {DynamicData} from "../../../DynamicData";

export class FactoryListAttributeEditor extends AttributeEditorWidget{

    constructor(protected attributeAccessor: AttributeAccessor<any>, protected inputId: string, private factoryEditorModel: FactoryEditorModel, private httpClient: HttpClient) {
        super(attributeAccessor,inputId);
    }

    protected render(): HTMLElement{
        let div: HTMLElement= document.createElement("div");
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
        ul.hidden = values.length===0;

        let newButton: HTMLButtonElement = BootstrapUtility.createButtonPrimary();
        newButton.textContent="add";
        newButton.onclick=(e)=>{

            this.httpClient.createNewFactories(
                this.factoryEditorModel.getFactory().getId(),
                this.attributeAccessor.getAttributeName(),
                this.factoryEditorModel.getFactory().getRoot(),(possibleValues: Data[])=>{
                    if (possibleValues.length>1){
                        let dialog: FactorySelectDialog = new FactorySelectDialog(newButton.parentElement!,possibleValues,(selected: Data)=>{
                            let items = this.attributeAccessor.getValue();
                            items.push(selected);
                            this.attributeAccessor.setValue(items);
                        });
                        dialog.show();
                    } else {
                        let items = this.attributeAccessor.getValue();
                        items.push(possibleValues[0]);
                        this.attributeAccessor.setValue(items);
                    }



                    // this.factoryEditor.updateTree();
                }
            )

        };


        div.appendChild(ul);
        div.appendChild(newButton);

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
            let items: Data[] = this.attributeAccessor.getValue();
            items.splice(items.indexOf(value), 1);
            this.attributeAccessor.setValue(items);
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

    // protected bindAttribute(): any {
    //     this.reRender(this.renderAttribute());
    // }

}