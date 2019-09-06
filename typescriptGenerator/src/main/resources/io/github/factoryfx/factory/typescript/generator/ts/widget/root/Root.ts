import {Widget} from "../../base/Widget";
import {View} from "../view/View";
import {WaitAnimation} from "../waitannimation/WaitAnimation";
import {ErrorReporter} from "../errorreporter/ErrorReporter";


export class Root extends Widget {

    constructor(private readonly view: View, private readonly waitAnimation: WaitAnimation, private readonly errorReporter: ErrorReporter) {
        super();
    }


    render(): HTMLElement {
        let htmlDivElement = document.createElement("div");
        this.waitAnimation.append(htmlDivElement);
        this.view.append(htmlDivElement);
        this.errorReporter.append(htmlDivElement);
        return htmlDivElement;
    }
}