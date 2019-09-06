//generated code don't edit manually
import { Value } from "./Value";
export class AttributeAccessorValue extends Value {
    constructor() {
        super();
        this.listener = () => {
            if (this.parent) {
                this.parent.update();
            }
        };
    }
    set(attributeAccessor) {
        let previous = this.get();
        if (previous) {
            previous.removeChangeListener(this.listener);
        }
        if (attributeAccessor) {
            attributeAccessor.addChangeListener(this.listener);
        }
        super.set(attributeAccessor);
    }
}
