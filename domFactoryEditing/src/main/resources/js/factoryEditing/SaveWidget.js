import { HttpUtility } from "./HttpUtility";
import { FactoryUpdateResult } from "./FactoryUpdateResult";
export class SaveWidget {
    constructor(root, baseVersionId, view, waitAnimation) {
        this.root = root;
        this.baseVersionId = baseVersionId;
        this.view = view;
        this.waitAnimation = waitAnimation;
    }
    create() {
        let div = document.createElement("div");
        div.className = "alert alert alert-warning";
        div.setAttribute("role", "alert");
        let h = document.createElement("h4");
        h.textContent = "Save changes";
        let form = document.createElement("form");
        let formGroup = document.createElement("div");
        formGroup.className = "form-group";
        let label = document.createElement("label");
        label.textContent = "Comment";
        label.htmlFor = "textarea";
        let textarea = document.createElement("textarea");
        textarea.id = "textarea";
        textarea.className = "form-control";
        formGroup.appendChild(label);
        formGroup.appendChild(textarea);
        form.appendChild(formGroup);
        let saveButton = document.createElement("button");
        saveButton.className = "btn btn-outline-success";
        saveButton.textContent = "Save";
        saveButton.onclick = (e) => {
            let saveRequestBody = {
                "user": "",
                "passwordHash": "",
                "request": {
                    "@class": "io.github.factoryfx.factory.storage.DataUpdate",
                    "root": this.root.mapToJson({}),
                    "user": "1",
                    "comment": textarea.value,
                    "baseVersionId": this.baseVersionId
                }
            };
            HttpUtility.post("updateCurrentFactory", saveRequestBody, this.waitAnimation, (response) => {
                this.view.show(new FactoryUpdateResult(response));
            });
        };
        div.appendChild(h);
        div.appendChild(formGroup);
        div.appendChild(saveButton);
        return div;
    }
}
