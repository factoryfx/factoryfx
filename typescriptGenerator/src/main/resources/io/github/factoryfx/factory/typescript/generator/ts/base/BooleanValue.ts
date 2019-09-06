import {Value} from "./Value";
import {WidgetModel} from "./WidgetModel";

export class BooleanValue extends Value<boolean> {

    getChildren(): WidgetModel<any>[] {
        return [];
    }

}