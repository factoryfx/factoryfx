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
        if (this.parentElement.firstElementChild) {
            this.content = this.parentElement.firstElementChild;
            this.parentElement.removeChild(this.parentElement.firstElementChild);
        }
        this.parentElement.appendChild(div);
    }
    hide() {
        if (this.parentElement.firstElementChild) {
            this.parentElement.removeChild(this.parentElement.firstElementChild);
            if (this.content) {
                this.parentElement.appendChild(this.content);
            }
        }
    }
}
