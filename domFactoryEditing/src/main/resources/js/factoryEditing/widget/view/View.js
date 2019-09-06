//generated code don't edit manually
import { Widget } from "../../base/Widget";
export class View extends Widget {
    constructor(model) {
        super();
        this.model = model;
    }
    render() {
        let htmlDivElement = document.createElement("div");
        if (!this.model.visible.get()) {
            return htmlDivElement;
        }
        this.model.navbar.getWidget().append(htmlDivElement);
        this.model.factoryEditor.getWidget().append(htmlDivElement);
        this.model.saveWidget.getWidget().append(htmlDivElement);
        this.model.factoryUpdateResult.getWidget().append(htmlDivElement);
        return htmlDivElement;
    }
}
