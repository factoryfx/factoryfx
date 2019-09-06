import {Widget} from "../../base/Widget";
import {WaitAnimationModel} from "./WaitAnimationModel";


export class WaitAnimation extends Widget{

    constructor(private model: WaitAnimationModel){
        super();
    }

    render(): HTMLElement {
        let div: HTMLDivElement = document.createElement("div");
        if (!this.model.visible.get()){
            return div;
        }
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
        // DomUtility.clear(this.parentElement);
        // this.parentElement.appendChild(this.create());
    }

    hide() {
        // DomUtility.clear(this.parentElement);
        // this.parentElement.appendChild(this.view.create());
        document.documentElement.scrollTop = document.body.scrollTop = this.scrollTop;
    }

    reportError(responseText: string) {
        //TODO
        console.log(responseText);
    }
}