import {AttributeEditor} from "./AttributeEditor";
import {AttributeType} from "./AttributeType";
import {AttributeAccessor} from "./AttributeAccessor";
import {AttributeEditorStringAttribute} from "./AttributeEditorStringAttribute";
import {AttributeEditorFallback} from "./AttributeEditorFallback";
import {FactoryEditor} from "./FactoryEditor";
import {AttributeEditorFactoryAttribute} from "./AttributeEditorFactoryAttribute";
import {AttributeEditorFactoryListAttribute} from "./AttributeEditorFactoryListAttribute";

export class AttributeEditorCreator {
    private attributeEditors: AttributeEditor[];

    constructor(attributeEditors: AttributeEditor[]) {
        if (!attributeEditors){
            this.attributeEditors=[];
            this.attributeEditors[AttributeType.StringAttribute]= (attributeAccessor: AttributeAccessor<any,any>, inputId: string,factoryEditor: FactoryEditor)=>new AttributeEditorStringAttribute(attributeAccessor,inputId);
            this.attributeEditors[AttributeType.FactoryAttribute]= (attributeAccessor: AttributeAccessor<any,any>, inputId: string,factoryEditor: FactoryEditor)=>new AttributeEditorFactoryAttribute(attributeAccessor,inputId,factoryEditor);
            this.attributeEditors[AttributeType.FactoryListAttribute]= (attributeAccessor: AttributeAccessor<any,any>, inputId: string,factoryEditor: FactoryEditor)=>new AttributeEditorFactoryListAttribute(attributeAccessor,inputId,factoryEditor);
        } else {
            this.attributeEditors=attributeEditors;
        }
    }


    create(attributeAccessor: AttributeAccessor<any,any>, inputId: string,factoryEditor: FactoryEditor): AttributeEditor{
        if (!this.attributeEditors[attributeAccessor.attributeMetadata.getType()]){
            return new AttributeEditorFallback(attributeAccessor,inputId);
        }
        return this.attributeEditors[attributeAccessor.attributeMetadata.getType()](attributeAccessor,inputId,factoryEditor);

    }
}