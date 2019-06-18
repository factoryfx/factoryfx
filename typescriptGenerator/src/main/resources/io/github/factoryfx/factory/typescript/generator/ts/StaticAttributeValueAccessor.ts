import {Data} from "./Data";
import {AttributeValueAccessor} from "./AttributeValueAccessor";

export class StaticAttributeValueAccessor<T> implements AttributeValueAccessor<T>{

    constructor(private attributeParent: Data, private attributeName: string) {
        this.attributeParent = attributeParent;
        this.attributeName = attributeName;
    }

    getValue(): T{
        return this.attributeParent[this.attributeName]
    }

    setValue(value: T){
        this.attributeParent[this.attributeName]=value;
    }
}