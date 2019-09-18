import {AttributeMetadata} from "./AttributeMetadata";

export class AttributeAccessor<T> {

    constructor(private attributeMetadata: AttributeMetadata<T>, private attributeValues: any, private attributeName: string) {

    }

    getValue(): T{
        return this.attributeValues[this.attributeName]
    }

    setValue(value: T){
        this.attributeValues[this.attributeName]=value;
        for (let listener of this.listeners){
            listener();
        }
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

    private listeners: Array<()=>any>= [];
    public addChangeListener(listener: ()=>any){
        if (!this.listeners.includes(listener)){
            this.listeners.push(listener);
        }
    }
    public removeChangeListener(listener: ()=>any){
        this.listeners.splice(this.listeners.indexOf(listener),1);
    }

}