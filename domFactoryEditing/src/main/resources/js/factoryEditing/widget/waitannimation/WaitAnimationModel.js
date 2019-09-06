//generated code don't edit manually
import { WidgetModel } from "../../base/WidgetModel";
import { WaitAnimation } from "./WaitAnimation";
import { BooleanValue } from "../../base/BooleanValue";
export class WaitAnimationModel extends WidgetModel {
    constructor() {
        super(...arguments);
        this.visible = new BooleanValue();
    }
    createWidget() {
        return new WaitAnimation(this);
    }
}
