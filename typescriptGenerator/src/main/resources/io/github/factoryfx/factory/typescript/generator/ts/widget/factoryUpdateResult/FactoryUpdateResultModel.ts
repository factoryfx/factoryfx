import {WidgetModel} from "../../base/WidgetModel";
import {FactoryUpdateResult} from "./FactoryUpdateResult";
import {BooleanValue} from "../../base/BooleanValue";
import {StringValue} from "../../base/StringValue";


export class FactoryUpdateResultModel  extends WidgetModel<FactoryUpdateResult> {

    public readonly visible: BooleanValue = new BooleanValue();
    public readonly updatelog: StringValue = new StringValue();

    protected createWidget(): FactoryUpdateResult {
        return new FactoryUpdateResult(this);
    }
}