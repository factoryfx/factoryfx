//generated code don't edit manually
import { WidgetModel } from "../../base/WidgetModel";
import { View } from "./View";
import { BooleanValue } from "../../base/BooleanValue";
export class ViewModel extends WidgetModel {
    constructor(factoryEditor, saveWidget, factoryUpdateResult, navbar) {
        super();
        this.factoryEditor = factoryEditor;
        this.saveWidget = saveWidget;
        this.factoryUpdateResult = factoryUpdateResult;
        this.navbar = navbar;
        this.visible = new BooleanValue();
    }
    showFactoryEditor() {
        this.factoryEditor.visible.set(true);
        this.saveWidget.visible.set(false);
        this.factoryUpdateResult.visible.set(false);
    }
    showSaveContent() {
        this.factoryEditor.visible.set(false);
        this.saveWidget.visible.set(true);
        this.factoryUpdateResult.visible.set(false);
    }
    showFactoryUpdateResult() {
        this.factoryEditor.visible.set(false);
        this.saveWidget.visible.set(false);
        this.factoryUpdateResult.visible.set(true);
    }
    createWidget() {
        return new View(this);
    }
}
