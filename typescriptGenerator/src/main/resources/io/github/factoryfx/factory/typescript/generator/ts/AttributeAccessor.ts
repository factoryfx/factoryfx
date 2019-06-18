import {AttributeMetadata} from "./AttributeMetadata";
import {Data} from "./Data";
import {AttributeValueAccessor} from "./AttributeValueAccessor";

export class AttributeAccessor<T> {

    constructor(private attributeMetadata: AttributeMetadata<T>, private valueAccessor: AttributeValueAccessor<T>, private attributeName: string) {

    }

    getValue(): T{
        return this.valueAccessor.getValue();
    }

    setValue(value: T){
        this.valueAccessor.setValue(value);
    }

    getLabelText(locale: string): string{
        let labelText: string = this.attributeMetadata.getLabelText(locale);
        if (!labelText){
            labelText=this.attributeName;
        }
        return labelText;
    }

    public getAttributeMetadata(): AttributeMetadata<T>{
        return this.attributeMetadata;
    }

    public getAttributeName(): string{
        return this.attributeName;
    }


}