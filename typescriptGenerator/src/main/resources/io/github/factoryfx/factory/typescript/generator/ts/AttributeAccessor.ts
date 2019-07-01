import {AttributeMetadata} from "./AttributeMetadata";

export class AttributeAccessor<T> {

    constructor(private attributeMetadata: AttributeMetadata<T>, private attributeValues: any, private attributeName: string) {

    }

    getValue(): T{
        return this.attributeValues[this.attributeName]
    }

    setValue(value: T){
        this.attributeValues[this.attributeName]=value;
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