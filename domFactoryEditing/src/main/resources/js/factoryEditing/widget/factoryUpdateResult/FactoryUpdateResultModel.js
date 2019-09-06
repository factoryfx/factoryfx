//generated code don't edit manually
import { WidgetModel } from "../../base/WidgetModel";
import { FactoryUpdateResult } from "./FactoryUpdateResult";
import { BooleanValue } from "../../base/BooleanValue";
import { StringValue } from "../../base/StringValue";
export class FactoryUpdateResultModel extends WidgetModel {
    constructor() {
        super(...arguments);
        this.visible = new BooleanValue();
        this.updatelog = new StringValue();
    }
    createWidget() {
        return new FactoryUpdateResult(this);
    }
}
