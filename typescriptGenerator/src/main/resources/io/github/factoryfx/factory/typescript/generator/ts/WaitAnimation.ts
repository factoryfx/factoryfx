import {AttributeEditorCreator} from "./AttributeEditorCreator";
import {Data} from "./Data";
import {ValidationError} from "./ValidationError";
import {DomUtility} from "./DomUtility";
import {View} from "./View";
import {Widget} from "./Widget";


export class WaitAnimation implements Widget{

    constructor(private parentElement: HTMLElement, private view: View) {

    }

    create(): HTMLElement {
        let div: HTMLDivElement = document.createElement("div");
        div.className="progress";

        let progressbarDiv: HTMLDivElement = document.createElement("div");
        progressbarDiv.className="progress-bar progress-bar-striped progress-bar-animated";
        progressbarDiv.setAttribute("role","progressbar");

        progressbarDiv.style.width="100%";

        div.appendChild(progressbarDiv);
        return div;
    }

    content: Element[]=[];

    scrollTop!: number;
    show(): void {
        this.scrollTop = window.pageYOffset || document.documentElement.scrollTop;
        DomUtility.clear(this.parentElement);
        this.parentElement.appendChild(this.create());
    }

    hide() {
        DomUtility.clear(this.parentElement);
        this.parentElement.appendChild(this.view.create());
        document.documentElement.scrollTop = document.body.scrollTop = this.scrollTop;
    }

    reportError(responseText: string) {
        //TODO
        console.log(responseText);
    }
}