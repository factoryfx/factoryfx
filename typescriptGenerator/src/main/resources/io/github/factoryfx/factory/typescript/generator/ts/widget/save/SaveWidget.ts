import {Widget} from "../../base/Widget";
import {SaveWidgetModel} from "./SaveWidgetModel";

export class SaveWidget extends Widget {

    constructor(private model: SaveWidgetModel){
        super();
    }

    render(): HTMLElement {
        let div: HTMLDivElement= document.createElement("div");
        if (!this.model.visible.get()){
            return div;
        }

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
            this.model.httpClient.updateCurrentFactory(
                this.model.rootFactory,
                this.model.baseVersionId,
                textarea.value,
                (response)=>{
                    this.model.viewModel!.factoryUpdateResult.updatelog.set(response.log);
                    this.model.viewModel!.showFactoryUpdateResult();
                // this.view.show(new FactoryUpdateResult(response));
            })
        };


        div.appendChild(h);
        div.appendChild(formGroup);
        div.appendChild(saveButton);
        return div;
    }


}