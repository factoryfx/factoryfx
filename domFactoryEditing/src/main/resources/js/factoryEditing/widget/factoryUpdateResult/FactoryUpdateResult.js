//generated code don't edit manually
import { Widget } from "../../base/Widget";
export class FactoryUpdateResult extends Widget {
    constructor(model) {
        super();
        this.model = model;
    }
    render() {
        let resultDisplay = document.createElement("div");
        if (!this.model.visible.get()) {
            return resultDisplay;
        }
        resultDisplay.className = "alert alert-success";
        resultDisplay.setAttribute("role", "alert");
        let pre = document.createElement("pre");
        let code = document.createElement("code");
        pre.appendChild(code);
        code.textContent = this.model.updatelog.get();
        resultDisplay.appendChild(pre);
        let button = document.createElement("button");
        button.className = "btn btn-outline-secondary";
        button.textContent = "continue";
        button.onclick = (e) => {
            window.location.reload();
        };
        resultDisplay.appendChild(button);
        return resultDisplay;
    }
}
