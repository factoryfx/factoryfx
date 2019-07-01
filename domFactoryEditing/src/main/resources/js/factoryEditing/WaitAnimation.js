import { DomUtility } from "./DomUtility";
export class WaitAnimation {
    constructor(parentElement, view) {
        this.parentElement = parentElement;
        this.view = view;
        this.content = [];
    }
    create() {
        let div = document.createElement("div");
        div.className = "progress";
        let progressbarDiv = document.createElement("div");
        progressbarDiv.className = "progress-bar progress-bar-striped progress-bar-animated";
        progressbarDiv.setAttribute("role", "progressbar");
        progressbarDiv.style.width = "100%";
        div.appendChild(progressbarDiv);
        return div;
    }
    show() {
        this.scrollTop = window.pageYOffset || document.documentElement.scrollTop;
        DomUtility.clear(this.parentElement);
        this.parentElement.appendChild(this.create());
    }
    hide() {
        DomUtility.clear(this.parentElement);
        this.parentElement.appendChild(this.view.create());
        document.documentElement.scrollTop = document.body.scrollTop = this.scrollTop;
    }
    reportError(responseText) {
        //TODO
        console.log(responseText);
    }
}
