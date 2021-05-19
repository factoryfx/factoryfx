import {AttributeEditorWidget} from "../AttributeEditorWidget";
import {AttributeAccessor} from "../../../AttributeAccessor";
import {FactoryEditorModel} from "../../factoryeditor/FactoryEditorModel";
import {HttpClient} from "../../../HttpClient";
import {FactorySelectDialog} from "../../../utility/FactorySelectDialog";
import {Data} from "../../../Data";
import {DynamicData} from "../../../DynamicData";
import {DynamicDataDictionary} from "../../../DynamicDataDictionary";

export class FactoryAttributeEditor extends AttributeEditorWidget{


    private editButton: HTMLButtonElement= document.createElement("button");
    private input: HTMLInputElement= document.createElement("input");


    constructor(protected attributeAccessor: AttributeAccessor<any>, protected inputId: string, private factoryEditorNode: FactoryEditorModel, private httpClient: HttpClient) {
        super(attributeAccessor,inputId);
        this.editButton.type="button";
        this.editButton.textContent="edit";
        this.editButton.className="btn btn-outline-secondary";

        this.input.id=this.inputId.toString();
        this.input.className="form-control";
        this.input.readOnly=true;
        this.input.required=!this.attributeAccessor.getAttributeMetadata().nullable();
    }

    protected render(): HTMLElement {
        let inputGroup: HTMLElement= document.createElement("div");
        inputGroup.className="input-group";

        this.bindValue();

        let inputGroupAppend: HTMLElement= document.createElement("div");
        inputGroupAppend.className="input-group-append";

        let removeButton: HTMLButtonElement= document.createElement("button");
        removeButton.type="button";
        removeButton.textContent="remove";
        removeButton.onclick=(e)=>{
            this.attributeAccessor.setValue(null);
        };
        removeButton.disabled=!this.attributeAccessor.getAttributeMetadata().nullable();
        removeButton.className="btn btn-outline-danger";


        let newButton: HTMLButtonElement= document.createElement("button");
        newButton.type="button";
        newButton.textContent="new";
        newButton.onclick=(e)=>{

            this.httpClient.createNewFactories(
                this.factoryEditorNode.getFactory().getId(),
                this.attributeAccessor.getAttributeName(),
                this.factoryEditorNode.getFactory().getRoot(),
                (possibleValues: Data[])=>{
                    if (possibleValues.length>1){
                        let dialog: FactorySelectDialog = new FactorySelectDialog(newButton.parentElement!,possibleValues,(selected: Data)=>{
                            this.attributeAccessor.setValue(selected);
                        });
                        dialog.show();
                    } else {
                        this.attributeAccessor.setValue(possibleValues[0]);
                    }
                    this.bindValue();

                }
            )

        };
        newButton.className="btn btn-outline-secondary";


        inputGroupAppend.appendChild(newButton);
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
            if (this.factoryEditorNode!.getWidget()!.validate()){
                this.factoryEditorNode.edit(value);
            }
        };
        if (!value) {
            this.editButton.disabled = true;
        }

        this.input.ondblclick=null;
        if (value) {
            this.input.ondblclick = (e) => {
                if (this.factoryEditorNode!.getWidget()!.validate()){
                    this.factoryEditorNode.edit(value);
                }
            }
        }
    }

}