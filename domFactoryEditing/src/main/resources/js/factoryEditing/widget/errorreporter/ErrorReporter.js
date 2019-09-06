//generated code don't edit manually
import { Widget } from "../../base/Widget";
export class ErrorReporter extends Widget {
    constructor(model) {
        super();
        this.model = model;
    }
    render() {
        let div = document.createElement("div");
        if (!this.model.visible.get()) {
            return div;
        }
        div.className = "alert alert-danger";
        div.textContent = this.model.errorText.get();
        div.style.whiteSpace = "pre";
        return div;
    }
}
