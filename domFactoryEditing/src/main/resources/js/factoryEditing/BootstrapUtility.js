//generated code don't edit manually
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
        let progress = document.createElement("div");
        progress.className = "progress";
        let progressbarDiv = document.createElement("div");
        progressbarDiv.className = "progress-bar progress-bar-striped progress-bar-animated";
        progressbarDiv.setAttribute("role", "progressbar");
        progressbarDiv.style.width = "100%";
        progress.append(progressbarDiv);
        return progress;
    }
    static createButtonPrimary() {
        let button = document.createElement("button");
        button.type = "button";
        button.className = "btn btn-primary";
        return button;
    }
    static createButtonSuccess() {
        let button = document.createElement("button");
        button.type = "button";
        button.className = "btn btn-success";
        return button;
    }
    static createButtonSecondary() {
        let button = document.createElement("button");
        button.type = "button";
        button.className = "btn btn-secondary";
        return button;
    }
}
