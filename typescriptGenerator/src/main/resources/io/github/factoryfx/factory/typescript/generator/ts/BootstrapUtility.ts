import {AttributeMetadata} from "./AttributeMetadata";

export class BootstrapUtility {

    static createInputGroup(input: HTMLInputElement, button: HTMLButtonElement): HTMLElement  {
        let inputGroup: HTMLElement = document.createElement("div");
        inputGroup.className = "input-group";
        inputGroup.style.marginRight = "3px";
        input.className = "form-control";

        let inputGroupAppend: HTMLElement = document.createElement("div");
        inputGroupAppend.className = "input-group-append";
        button.type = "button";
        button.className = "btn btn-outline-secondary";

        inputGroupAppend.appendChild(button);
        inputGroup.appendChild(input);
        inputGroup.appendChild(inputGroupAppend);
        return inputGroup;
    }

    static createProgressBar(): HTMLElement  {
        let progress: HTMLDivElement = document.createElement("div");
        progress.className="progress";

        let progressbarDiv: HTMLDivElement = document.createElement("div");
        progressbarDiv.className="progress-bar progress-bar-striped progress-bar-animated";
        progressbarDiv.setAttribute("role","progressbar");

        progressbarDiv.style.width="100%";

        progress.append(progressbarDiv);
        return progress;
    }

    static createButtonPrimary(): HTMLButtonElement  {
        let button: HTMLButtonElement = document.createElement("button");
        button.type="button";
        button.className="btn btn-primary";
        return button;
    }

    static createButtonSuccess(): HTMLButtonElement  {
        let button: HTMLButtonElement = document.createElement("button");
        button.type="button";
        button.className="btn btn-success";
        return button;
    }

    static createButtonSecondary(): HTMLButtonElement  {
        let button: HTMLButtonElement = document.createElement("button");
        button.type="button";
        button.className="btn btn-secondary";
        return button;
    }


}