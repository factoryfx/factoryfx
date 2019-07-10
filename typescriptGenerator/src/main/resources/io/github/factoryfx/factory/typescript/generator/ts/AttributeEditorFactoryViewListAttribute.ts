import {AttributeAccessor} from "./AttributeAccessor";
import {AttributeEditor} from "./AttributeEditor";
import {FactoryEditor} from "./FactoryEditor";
import {Data} from "./Data";
import {WaitAnimation} from "./WaitAnimation";
import {HttpUtility} from "./HttpUtility";
import {DomUtility} from "./DomUtility";

export class AttributeEditorFactoryViewListAttribute implements AttributeEditor{

    constructor(private attributeAccessor: AttributeAccessor<any>, private inputId: string, private factoryEditor: FactoryEditor, private waitAnimation: WaitAnimation) {
    }

    create(): HTMLElement{
        let content: HTMLElement= document.createElement("div");

        let loadButton: HTMLButtonElement= document.createElement("button");
        loadButton.type="button";
        loadButton.textContent="view";
        loadButton.className="btn btn-primary";
        loadButton.onclick=(e)=>{
            let root = this.factoryEditor.getCurrentData().getRoot();
            let request={
                factoryId: this.factoryEditor.getCurrentData().getId(),
                attributeVariableName: this.attributeAccessor.getAttributeName(),
                root: root.mapToJsonFromRoot(),
            };
            HttpUtility.post("resolveViewList",request,this.waitAnimation,(response: any)=>{
                DomUtility.clear(content);
                content.appendChild(this.createFactoryList(response));
            });
        };
        content.appendChild(loadButton);
        return content;
    }

    private createFactoryList(factoryIds: string[]): HTMLElement{
        let root = this.factoryEditor.getCurrentData().getRoot();

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
            this.factoryEditor.edit(value)
        };
        editButton.className="btn btn-outline-secondary";
        inputGroupAppend.append(editButton);

        inputGroup.appendChild(input);
        inputGroup.appendChild(inputGroupAppend);
        return  inputGroup;
    }


}