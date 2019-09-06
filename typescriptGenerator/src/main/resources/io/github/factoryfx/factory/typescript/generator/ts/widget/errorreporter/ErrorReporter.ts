import {Widget} from "../../base/Widget";
import {ErrorReporterModel} from "./ErrorReporterModel";


export class ErrorReporter extends Widget{

    constructor(private model: ErrorReporterModel){
        super();
    }

    render(): HTMLElement {
        let div: HTMLDivElement = document.createElement("div");
        if (!this.model.visible.get()){
            return div;
        }
        div.className="alert alert-danger";
        div.textContent=this.model.errorText.get()!;
        div.style.whiteSpace="pre";
        return div;
    }

}