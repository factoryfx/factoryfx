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
        // let previous: Data=this.get()!;
        // if (previous){
        //     for (let child of previous.collectChildren()) {
        //         child.removeChangeListener(this.listener);
        //     }
        // }
        // if (value){
        //     for (let child of value.collectChildren()) {
        //         child.addChangeListener(this.listener);
        //     }
        // }
        super.set(value);
    }
}
