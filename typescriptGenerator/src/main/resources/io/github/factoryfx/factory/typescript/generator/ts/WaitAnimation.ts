import {AttributeEditorCreator} from "./AttributeEditorCreator";
import {Data} from "./Data";
import {FactoryChangeEvent} from "./FactoryChangeEvent";
import {ValidationError} from "./ValidationError";


export class WaitAnimation {
    constructor(private parentElement: HTMLElement) {

    }

    content: Element;
    show(): void {


        let div: HTMLDivElement = document.createElement("div");
        div.className="progress";

        let progressbarDiv: HTMLDivElement = document.createElement("div");
        progressbarDiv.className="progress-bar progress-bar-striped progress-bar-animated";
        progressbarDiv.setAttribute("role","progressbar");

        progressbarDiv.style.width="100%";

        div.appendChild(progressbarDiv);

        if (this.parentElement.firstElementChild) {
            this.content = this.parentElement.firstElementChild;
            this.parentElement.removeChild(this.parentElement.firstElementChild);
        }
        this.parentElement.appendChild(div);

    }

    hide(){
        if (this.parentElement.firstElementChild){
            this.parentElement.removeChild(this.parentElement.firstElementChild);
            if (this.content){
                this.parentElement.appendChild(this.content);
            }
        }
    }


}