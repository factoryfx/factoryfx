export class WaitAnimation {
    constructor(parentElement) {
        this.parentElement = parentElement;
    }
    show() {
        let div = document.createElement("div");
        div.className = "progress";
        let progressbarDiv = document.createElement("div");
        progressbarDiv.className = "progress-bar progress-bar-striped progress-bar-animated";
        progressbarDiv.setAttribute("role", "progressbar");
        progressbarDiv.style.width = "100%";
        div.appendChild(progressbarDiv);
        this.parentElement.appendChild(div);
    }
}
