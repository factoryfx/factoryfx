import {AttributeEditorCreator} from "./AttributeEditorCreator";
import {Data} from "./Data";
import {FactoryChangeEvent} from "./FactoryChangeEvent";
import {ValidationError} from "./ValidationError";


export class WaitAnimation {
    constructor(private parentElement: HTMLElement) {

    }

    show(): void {
        let div: HTMLDivElement = document.createElement("div");
        div.className="progress";

        let progressbarDiv: HTMLDivElement = document.createElement("div");
        progressbarDiv.className="progress-bar progress-bar-striped progress-bar-animated";
        progressbarDiv.setAttribute("role","progressbar");

        progressbarDiv.style.width="100%";

        div.appendChild(progressbarDiv);
        this.parentElement.appendChild(div)

    }


}