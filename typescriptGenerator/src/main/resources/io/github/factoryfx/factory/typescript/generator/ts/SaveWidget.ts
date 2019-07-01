import {Widget} from "./Widget";
import {HttpUtility} from "./HttpUtility";
import {FactoryUpdateResult} from "./FactoryUpdateResult";
import {Data} from "./Data";
import {View} from "./View";
import {WaitAnimation} from "./WaitAnimation";

export class SaveWidget implements Widget {

    constructor(private root: Data, private baseVersionId: string, private view: View, private waitAnimation: WaitAnimation){

    }

    create(): HTMLElement {
        let div: HTMLDivElement= document.createElement("div");
        div.className="alert alert alert-warning";
        div.setAttribute("role","alert");

        let h: HTMLHeadingElement= document.createElement("h4");
        h.textContent="Save changes";


        let form : HTMLFormElement= document.createElement("form");
        let formGroup: HTMLDivElement= document.createElement("div");
        formGroup.className="form-group";

        let label: HTMLLabelElement= document.createElement("label");
        label.textContent="Comment";
        label.htmlFor="textarea";

        let textarea: HTMLTextAreaElement= document.createElement("textarea");
        textarea.id="textarea";
        textarea.className="form-control";
        formGroup.appendChild(label);
        formGroup.appendChild(textarea);
        form.appendChild(formGroup);


        let saveButton: HTMLButtonElement = document.createElement("button");
        saveButton.className="btn btn-outline-success";
        saveButton.textContent="Save";
        saveButton.onclick=(e)=>{
            let saveRequestBody = {
                "user": "",
                "passwordHash": "",
                "request": {
                    "@class": "io.github.factoryfx.factory.storage.DataUpdate",
                    "root": this.root.mapToJson({}),
                    "user": "1",
                    "comment": textarea.value,
                    "baseVersionId": this.baseVersionId
                }
            };
            HttpUtility.post("updateCurrentFactory",saveRequestBody,this.waitAnimation,(response)=>{
                this.view.show(new FactoryUpdateResult(response));
            });
        };


        div.appendChild(h);
        div.appendChild(formGroup);
        div.appendChild(saveButton);
        return div;
    }
}