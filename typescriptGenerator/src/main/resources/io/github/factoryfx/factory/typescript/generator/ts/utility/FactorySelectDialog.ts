import {WidgetModel} from "../base/WidgetModel";
import {AttributeEditorWidget} from "../widget/attribute/AttributeEditorWidget";
import {AttributeAccessorValue} from "../base/AttributeAccessorValue";
import {StringValue} from "../base/StringValue";
import {Data} from "../Data";
import {BootstrapUtility} from "../BootstrapUtility";

declare var dialogPolyfill: any;

export class FactorySelectDialog{

    constructor(private parent :HTMLElement, private possibleValues: Data[], private selectCallback: (selected: Data)=>any){


    }

    public show(){
        let dialog: HTMLDialogElement = document.createElement("dialog");
        dialogPolyfill.registerDialog(dialog);
        this.parent.appendChild(dialog);

        let list: HTMLUListElement = document.createElement("ul");
        list.className="list-group";
        dialog.appendChild(list);


        for (let possibleValue of this.possibleValues) {
            let li: HTMLLIElement = document.createElement("li");
            list.appendChild(li);
            li.className="list-group-item";

            let selectButton: HTMLButtonElement = BootstrapUtility.createButtonSuccess();
            li.appendChild(selectButton);
            selectButton.style.marginLeft="6px";
            selectButton.onclick=(e)=>{
                this.selectCallback(possibleValue);
                dialog.close();
            };
            selectButton.textContent=possibleValue.getDisplayText();
        }

        let closeButton: HTMLButtonElement = BootstrapUtility.createButtonSecondary();
        dialog.appendChild(closeButton);
        closeButton.style.marginTop="6px";
        closeButton.onclick=(e)=>{
            dialog.close();
        };
        closeButton.textContent="Cancel"

        dialog.showModal();
    }


}