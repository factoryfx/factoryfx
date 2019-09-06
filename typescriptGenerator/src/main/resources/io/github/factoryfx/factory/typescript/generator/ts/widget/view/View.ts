import {Widget} from "../../base/Widget";
import {ViewModel} from "./ViewModel";


export class View extends Widget {
    constructor(private model: ViewModel) {
        super();
    }

    render(): HTMLElement {
        let htmlDivElement: HTMLDivElement = document.createElement("div");
        if (!this.model.visible.get()){
            return htmlDivElement;
        }
        this.model.navbar.getWidget().append(htmlDivElement);
        this.model.factoryEditor.getWidget().append(htmlDivElement);
        this.model.saveWidget.getWidget().append(htmlDivElement);
        this.model.factoryUpdateResult.getWidget().append(htmlDivElement);
        return htmlDivElement;
    }


}