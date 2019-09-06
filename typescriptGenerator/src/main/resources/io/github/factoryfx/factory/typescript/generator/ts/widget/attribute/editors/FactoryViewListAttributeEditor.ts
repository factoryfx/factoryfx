import {DomUtility} from "../../../DomUtility";
import {Data} from "../../../Data";
import {AttributeAccessor} from "../../../AttributeAccessor";
import {AttributeEditorWidget} from "../AttributeEditorWidget";
import {FactoryEditorModel} from "../../factoryeditor/FactoryEditorModel";
import {HttpClient} from "../../../HttpClient";

export class FactoryViewListAttributeEditor extends AttributeEditorWidget{

    constructor(protected attributeAccessor: AttributeAccessor<any>, protected inputId: string, private factoryEditorModel: FactoryEditorModel, private httpClient: HttpClient) {
        super(attributeAccessor,inputId);
    }

    protected renderAttribute(): HTMLElement{
        let content: HTMLElement= document.createElement("div");

        let loadButton: HTMLButtonElement= document.createElement("button");
        loadButton.type="button";
        loadButton.textContent="view";
        loadButton.className="btn btn-primary";
        loadButton.onclick=(e)=>{
            this.httpClient.resolveViewList(
                this.factoryEditorModel.getFactory().getId(),
                this.attributeAccessor.getAttributeName(),
                this.factoryEditorModel.getFactory().getRoot(),
                (response: any)=>{
                        DomUtility.clear(content);
                        content.appendChild(this.createFactoryList(response));
                    }
                );
        };
        content.appendChild(loadButton);
        return content;
    }

    private createFactoryList(factoryIds: string[]): HTMLElement{
        let root = this.factoryEditorModel.getFactory().getRoot();

        let ul: HTMLElement= document.createElement("ul");
        ul.setAttribute("class","list-group");
        for (let factoryId of factoryIds) {
            let li: HTMLElement = document.createElement("li");
            li.setAttribute("class", "list-group-item");
            li.appendChild(this.createFactoryListItem(root.getChildFromRoot(factoryId)));
            ul.appendChild(li);
        }
        ul.style.paddingTop="3px";
        return ul;
    }

    private createFactoryListItem(value: Data): HTMLElement{
        let inputGroup: HTMLElement= document.createElement("div");
        inputGroup.setAttribute("class","input-group");
        let input: HTMLInputElement= document.createElement("input");
        input.id=this.inputId.toString();
        input.className="form-control";
        input.readOnly=true;
        input.value=value.getDisplayText();

        let inputGroupAppend: HTMLElement= document.createElement("div");
        inputGroupAppend.className="input-group-append";
        let editButton: HTMLButtonElement= document.createElement("button");
        editButton.type="button";
        editButton.textContent="edit";
        editButton.onclick=(e)=>{
            this.factoryEditorModel.edit(value);
        };
        editButton.className="btn btn-outline-secondary";
        inputGroupAppend.append(editButton);

        inputGroup.appendChild(input);
        inputGroup.appendChild(inputGroupAppend);
        return  inputGroup;
    }


}