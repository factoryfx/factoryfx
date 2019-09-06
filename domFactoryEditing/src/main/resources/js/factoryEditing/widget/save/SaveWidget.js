//generated code don't edit manually
import { Widget } from "../../base/Widget";
export class SaveWidget extends Widget {
    constructor(model) {
        super();
        this.model = model;
    }
    render() {
        let div = document.createElement("div");
        if (!this.model.visible.get()) {
            return div;
        }
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
            this.model.httpClient.updateCurrentFactory(this.model.rootFactory, "", "", this.model.baseVersionId, textarea.value, (response) => {
                this.model.viewModel.factoryUpdateResult.updatelog.set(response.log);
                this.model.viewModel.showFactoryUpdateResult();
                // this.view.show(new FactoryUpdateResult(response));
            });
        };
        div.appendChild(h);
        div.appendChild(formGroup);
        div.appendChild(saveButton);
        return div;
    }
}
