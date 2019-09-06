import {WidgetModel} from "../../base/WidgetModel";
import {WaitAnimation} from "./WaitAnimation";
import {BooleanValue} from "../../base/BooleanValue";


export class WaitAnimationModel  extends WidgetModel<WaitAnimation> {
    public readonly visible: BooleanValue = new BooleanValue();

    protected createWidget(): WaitAnimation {
        return new WaitAnimation(this);
    }
}