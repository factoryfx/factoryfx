import {Value} from "./Value";
import {AttributeAccessor} from "../AttributeAccessor";

export class AttributeAccessorValue extends Value<AttributeAccessor<any>> {

    private listener: ()=>any;
    constructor() {
        super();
        this.listener=()=> {
            if (this.parent) {
                this.parent.update();
            }
        }
    }

    set(attributeAccessor?: AttributeAccessor<any>){
        let previous: AttributeAccessor<any>=this.get()!;
        if (previous){
            previous.removeChangeListener(this.listener);
        }
        if (attributeAccessor){
            attributeAccessor.addChangeListener(this.listener);
        }
        super.set(attributeAccessor);
    }

}