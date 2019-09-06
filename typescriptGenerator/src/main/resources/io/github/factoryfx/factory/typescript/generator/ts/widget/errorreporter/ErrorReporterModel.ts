import {WidgetModel} from "../../base/WidgetModel";
import {BooleanValue} from "../../base/BooleanValue";
import {StringValue} from "../../base/StringValue";
import {ErrorReporter} from "./ErrorReporter";


export class ErrorReporterModel  extends WidgetModel<ErrorReporter> {
    public readonly visible: BooleanValue = new BooleanValue();
    public readonly errorText: StringValue = new StringValue();

    protected createWidget(): ErrorReporter {
        return new ErrorReporter(this);
    }
}