//generated code don't edit manually
import { WidgetModel } from "../../base/WidgetModel";
import { Navitem } from "./Navitem";
import { FactoryValue } from "../../base/FactoryValue";
export class NavitemModel extends WidgetModel {
    constructor(factoryData, factoryEditor) {
        super();
        this.factoryData = factoryData;
        this.factoryEditor = factoryEditor;
        this.factory = new FactoryValue();
        this.factory.set(factoryData);
    }
    createWidget() {
        return new Navitem(this);
    }
}
