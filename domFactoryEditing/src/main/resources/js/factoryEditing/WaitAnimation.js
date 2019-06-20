export class WaitAnimation {
    constructor(parentElement) {
        this.parentElement = parentElement;
        this.content = [];
    }
    show() {
        let div = document.createElement("div");
        div.className = "progress";
        let progressbarDiv = document.createElement("div");
        progressbarDiv.className = "progress-bar progress-bar-striped progress-bar-animated";
        progressbarDiv.setAttribute("role", "progressbar");
        progressbarDiv.style.width = "100%";
        div.appendChild(progressbarDiv);
        this.content = [];
        for (let i = 0; i < this.parentElement.children.length; i++) {
            this.content.push(this.parentElement.children.item(i));
        }
        this.clear();
        this.parentElement.appendChild(div);
    }
    hide() {
        if (this.parentElement.firstElementChild) {
            this.clear();
            if (this.content) {
                for (let element of this.content) {
                    this.parentElement.appendChild(element);
                }
            }
        }
    }
    clear() {
        while (this.parentElement.firstChild) {
            this.parentElement.removeChild(this.parentElement.firstChild);
        }
    }
}
