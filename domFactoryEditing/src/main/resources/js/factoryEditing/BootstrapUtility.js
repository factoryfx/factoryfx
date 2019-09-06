export class BootstrapUtility {
    static createInputGroup(input, button) {
        let inputGroup = document.createElement("div");
        inputGroup.className = "input-group";
        inputGroup.style.marginRight = "3px";
        input.className = "form-control";
        let inputGroupAppend = document.createElement("div");
        inputGroupAppend.className = "input-group-append";
        button.type = "button";
        button.className = "btn btn-outline-secondary";
        inputGroupAppend.appendChild(button);
        inputGroup.appendChild(input);
        inputGroup.appendChild(inputGroupAppend);
        return inputGroup;
    }
    static createProgressBar() {
        let progressbarDiv = document.createElement("div");
        progressbarDiv.className = "progress-bar progress-bar-striped progress-bar-animated";
        progressbarDiv.setAttribute("role", "progressbar");
        progressbarDiv.style.width = "100%";
        return progressbarDiv;
    }
}
