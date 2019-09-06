//generated code don't edit manually
import { WidgetModel } from "../../base/WidgetModel";
import { BooleanValue } from "../../base/BooleanValue";
import { StringValue } from "../../base/StringValue";
import { ErrorReporter } from "./ErrorReporter";
export class ErrorReporterModel extends WidgetModel {
    constructor() {
        super(...arguments);
        this.visible = new BooleanValue();
        this.errorText = new StringValue();
    }
    createWidget() {
        return new ErrorReporter(this);
    }
}
