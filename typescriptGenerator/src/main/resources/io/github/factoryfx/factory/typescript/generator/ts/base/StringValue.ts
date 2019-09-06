import {Value} from "./Value";
import {WidgetModel} from "./WidgetModel";

export class  StringValue extends Value<string> {

    getChildren(): WidgetModel<any>[] {
        return [];
    }

}