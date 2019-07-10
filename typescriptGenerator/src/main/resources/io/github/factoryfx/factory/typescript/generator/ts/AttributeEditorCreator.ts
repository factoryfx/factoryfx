import {AttributeEditor} from "./AttributeEditor";
import {AttributeType} from "./AttributeType";
import {AttributeAccessor} from "./AttributeAccessor";
import {AttributeEditorFallback} from "./AttributeEditorFallback";
import {FactoryEditor} from "./FactoryEditor";
import {AttributeEditorFactoryAttribute} from "./AttributeEditorFactoryAttribute";
import {AttributeEditorFactoryListAttribute} from "./AttributeEditorFactoryListAttribute";
import {AttributeEditorIntegerAttribute} from "./AttributeEditorIntegerAttribute";
import {AttributeEditorEnumAttribute} from "./AttributeEditorEnumAttribute";
import {AttributeEditorEnumListAttribute} from "./AttributeEditorEnumListAttribute";
import {AttributeEditorLongAttribute} from "./AttributeEditorLongAttribute";
import {AttributeEditorLocalDateAttribute} from "./AttributeEditorLocalDateAttribute";
import {AttributeEditorBooleanAttribute} from "./AttributeEditorBooleanAttribute";
import {AttributeEditorDoubleAttribute} from "./AttributeEditorDoubleAttribute";
import {AttributeEditorFileContentAttribute} from "./AttributeEditorFileContentAttribute";
import {AttributeEditorStringAttribute} from "./AttributeEditorStringAttribute";
import {AttributeEditorStringListAttribute} from "./AttributeEditorStringListAttribute";
import {WaitAnimation} from "./WaitAnimation";
import {AttributeEditorFactoryViewAttribute} from "./AttributeEditorFactoryViewAttribute";
import {AttributeEditorFactoryViewListAttribute} from "./AttributeEditorFactoryViewListAttribute";
import {AttributeEditorByteAttribute} from "./AttributeEditorByteAttribute";
import {AttributeEditorFloatAttribute} from "./AttributeEditorFloatAttribute";
import {AttributeEditorEncryptedStringAttribute} from "./AttributeEditorEncryptedStringAttribute";


export class AttributeEditorCreator {
    private attributeEditors: any;

    constructor(attributeEditors: AttributeEditor[]) {
        if (!attributeEditors){
            this.attributeEditors=[];
            this.attributeEditors[AttributeType.StringAttribute]= (attributeAccessor: AttributeAccessor<any>, inputId: string,factoryEditor: FactoryEditor, waitAnimation: WaitAnimation)=>new AttributeEditorStringAttribute(attributeAccessor,inputId);
            this.attributeEditors[AttributeType.IntegerAttribute]= (attributeAccessor: AttributeAccessor<any>, inputId: string,factoryEditor: FactoryEditor, waitAnimation: WaitAnimation)=>new AttributeEditorIntegerAttribute(attributeAccessor,inputId);
            this.attributeEditors[AttributeType.LongAttribute]= (attributeAccessor: AttributeAccessor<any>, inputId: string,factoryEditor: FactoryEditor, waitAnimation: WaitAnimation)=>new AttributeEditorLongAttribute(attributeAccessor,inputId);
            this.attributeEditors[AttributeType.EnumAttribute]= (attributeAccessor: AttributeAccessor<any>, inputId: string,factoryEditor: FactoryEditor, waitAnimation: WaitAnimation)=>new AttributeEditorEnumAttribute(attributeAccessor,inputId);
            this.attributeEditors[AttributeType.EnumListAttribute]= (attributeAccessor: AttributeAccessor<any>, inputId: string,factoryEditor: FactoryEditor, waitAnimation: WaitAnimation)=>new AttributeEditorEnumListAttribute(attributeAccessor,inputId);
            this.attributeEditors[AttributeType.LocalDateAttribute]= (attributeAccessor: AttributeAccessor<any>, inputId: string,factoryEditor: FactoryEditor, waitAnimation: WaitAnimation)=>new AttributeEditorLocalDateAttribute(attributeAccessor,inputId);
            this.attributeEditors[AttributeType.BooleanAttribute]= (attributeAccessor: AttributeAccessor<any>, inputId: string,factoryEditor: FactoryEditor, waitAnimation: WaitAnimation)=>new AttributeEditorBooleanAttribute(attributeAccessor,inputId);
            this.attributeEditors[AttributeType.DoubleAttribute]= (attributeAccessor: AttributeAccessor<any>, inputId: string,factoryEditor: FactoryEditor, waitAnimation: WaitAnimation)=>new AttributeEditorDoubleAttribute(attributeAccessor,inputId);
            this.attributeEditors[AttributeType.ByteAttribute]= (attributeAccessor: AttributeAccessor<any>, inputId: string,factoryEditor: FactoryEditor, waitAnimation: WaitAnimation)=>new AttributeEditorByteAttribute(attributeAccessor,inputId);
            this.attributeEditors[AttributeType.FileContentAttribute]= (attributeAccessor: AttributeAccessor<any>, inputId: string,factoryEditor: FactoryEditor, waitAnimation: WaitAnimation)=>new AttributeEditorFileContentAttribute(attributeAccessor,inputId);
            this.attributeEditors[AttributeType.StringListAttribute]= (attributeAccessor: AttributeAccessor<any>, inputId: string,factoryEditor: FactoryEditor, waitAnimation: WaitAnimation)=>new AttributeEditorStringListAttribute(attributeAccessor,inputId);
            this.attributeEditors[AttributeType.FloatAttribute]= (attributeAccessor: AttributeAccessor<any>, inputId: string,factoryEditor: FactoryEditor, waitAnimation: WaitAnimation)=>new AttributeEditorFloatAttribute(attributeAccessor,inputId);
            this.attributeEditors[AttributeType.EncryptedStringAttribute]= (attributeAccessor: AttributeAccessor<any>, inputId: string,factoryEditor: FactoryEditor, waitAnimation: WaitAnimation)=>new AttributeEditorEncryptedStringAttribute(attributeAccessor,inputId,factoryEditor,waitAnimation);


            this.attributeEditors[AttributeType.FactoryAttribute]= (attributeAccessor: AttributeAccessor<any>, inputId: string,factoryEditor: FactoryEditor, waitAnimation: WaitAnimation)=>new AttributeEditorFactoryAttribute(attributeAccessor,inputId,factoryEditor,waitAnimation);
            this.attributeEditors[AttributeType.FactoryListAttribute]= (attributeAccessor: AttributeAccessor<any>, inputId: string,factoryEditor: FactoryEditor, waitAnimation: WaitAnimation)=>new AttributeEditorFactoryListAttribute(attributeAccessor,inputId,factoryEditor,waitAnimation);
            this.attributeEditors[AttributeType.FactoryPolymorphicAttribute]= (attributeAccessor: AttributeAccessor<any>, inputId: string,factoryEditor: FactoryEditor, waitAnimation: WaitAnimation)=>new AttributeEditorFactoryAttribute(attributeAccessor,inputId,factoryEditor,waitAnimation);
            this.attributeEditors[AttributeType.FactoryPolymorphicListAttribute]= (attributeAccessor: AttributeAccessor<any>, inputId: string,factoryEditor: FactoryEditor, waitAnimation: WaitAnimation)=>new AttributeEditorFactoryListAttribute(attributeAccessor,inputId,factoryEditor,waitAnimation);
            this.attributeEditors[AttributeType.FactoryViewAttribute]= (attributeAccessor: AttributeAccessor<any>, inputId: string,factoryEditor: FactoryEditor, waitAnimation: WaitAnimation)=>new AttributeEditorFactoryViewAttribute(attributeAccessor,inputId,factoryEditor,waitAnimation);
            this.attributeEditors[AttributeType.FactoryViewListAttribute]= (attributeAccessor: AttributeAccessor<any>, inputId: string,factoryEditor: FactoryEditor, waitAnimation: WaitAnimation)=>new AttributeEditorFactoryViewListAttribute(attributeAccessor,inputId,factoryEditor,waitAnimation);


        } else {
            this.attributeEditors=attributeEditors;
        }
    }


    create(attributeAccessor: AttributeAccessor<any>, inputId: string,factoryEditor: FactoryEditor, waitAnimation: WaitAnimation): AttributeEditor{
        if (!this.attributeEditors[attributeAccessor.getAttributeMetadata().getType()]){
            return new AttributeEditorFallback(attributeAccessor,inputId);
        }
        return this.attributeEditors[attributeAccessor.getAttributeMetadata().getType()](attributeAccessor,inputId,factoryEditor,waitAnimation);

    }
}