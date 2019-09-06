//generated code don't edit manually
import { Widget } from "../../base/Widget";
export class Root extends Widget {
    constructor(view, waitAnimation, errorReporter) {
        super();
        this.view = view;
        this.waitAnimation = waitAnimation;
        this.errorReporter = errorReporter;
    }
    render() {
        let htmlDivElement = document.createElement("div");
        this.waitAnimation.append(htmlDivElement);
        this.view.append(htmlDivElement);
        this.errorReporter.append(htmlDivElement);
        return htmlDivElement;
    }
}
