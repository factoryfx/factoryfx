//generated code don't edit manually
import { Value } from "./Value";
export class FactoryValue extends Value {
    constructor() {
        super();
        this.listener = () => {
            if (this.parent) {
                this.parent.update();
            }
        };
    }
    getChildren() {
        return [];
    }
    set(value) {
        let previous = this.get();
        if (previous) {
            previous.removeChangeListener(this.listener);
        }
        if (value) {
            value.addChangeListener(this.listener);
        }
        super.set(value);
    }
}
