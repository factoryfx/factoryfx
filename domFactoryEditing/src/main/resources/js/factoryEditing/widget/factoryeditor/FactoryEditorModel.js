//generated code don't edit manually
import { WidgetModel } from "../../base/WidgetModel";
import { FactoryValue } from "../../base/FactoryValue";
import { FactoryEditor } from "./FactoryEditor";
import { BooleanValue } from "../../base/BooleanValue";
import { StringValue } from "../../base/StringValue";
export class FactoryEditorModel extends WidgetModel {
    constructor(httpClient) {
        super();
        this.httpClient = httpClient;
        this.visible = new BooleanValue();
        this.locale = new StringValue();
        this.factory = new FactoryValue();
    }
    getFactory() {
        return this.factory.get();
    }
    edit(factory) {
        this.factory.set(factory);
    }
    createWidget() {
        return new FactoryEditor(this, this.httpClient);
    }
}
