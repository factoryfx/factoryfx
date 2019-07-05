import { AttributeType } from "./AttributeType";
import { AttributeEditorFallback } from "./AttributeEditorFallback";
import { AttributeEditorFactoryAttribute } from "./AttributeEditorFactoryAttribute";
import { AttributeEditorFactoryListAttribute } from "./AttributeEditorFactoryListAttribute";
import { AttributeEditorIntegerAttribute } from "./AttributeEditorIntegerAttribute";
import { AttributeEditorEnumAttribute } from "./AttributeEditorEnumAttribute";
import { AttributeEditorEnumListAttribute } from "./AttributeEditorEnumListAttribute";
import { AttributeEditorLongAttribute } from "./AttributeEditorLongAttribute";
import { AttributeEditorLocalDateAttribute } from "./AttributeEditorLocalDateAttribute";
import { AttributeEditorBooleanAttribute } from "./AttributeEditorBooleanAttribute";
import { AttributeEditorDoubleAttribute } from "./AttributeEditorDoubleAttribute";
import { AttributeEditorFileContentAttribute } from "./AttributeEditorFileContentAttribute";
import { AttributeEditorStringAttribute } from "./AttributeEditorStringAttribute";
import { AttributeEditorStringListAttribute } from "./AttributeEditorStringListAttribute";
import { AttributeEditorFactoryViewAttribute } from "./AttributeEditorFactoryViewAttribute";
import { AttributeEditorFactoryViewListAttribute } from "./AttributeEditorFactoryViewListAttribute";
import { AttributeEditorByteAttribute } from "./AttributeEditorByteAttribute";
import { AttributeEditorFloatAttribute } from "./AttributeEditorFloatAttribute";
export class AttributeEditorCreator {
    constructor(attributeEditors) {
        if (!attributeEditors) {
            this.attributeEditors = [];
            this.attributeEditors[AttributeType.StringAttribute] = (attributeAccessor, inputId, factoryEditor, waitAnimation) => new AttributeEditorStringAttribute(attributeAccessor, inputId);
            this.attributeEditors[AttributeType.IntegerAttribute] = (attributeAccessor, inputId, factoryEditor, waitAnimation) => new AttributeEditorIntegerAttribute(attributeAccessor, inputId);
            this.attributeEditors[AttributeType.LongAttribute] = (attributeAccessor, inputId, factoryEditor, waitAnimation) => new AttributeEditorLongAttribute(attributeAccessor, inputId);
            this.attributeEditors[AttributeType.EnumAttribute] = (attributeAccessor, inputId, factoryEditor, waitAnimation) => new AttributeEditorEnumAttribute(attributeAccessor, inputId);
            this.attributeEditors[AttributeType.EnumListAttribute] = (attributeAccessor, inputId, factoryEditor, waitAnimation) => new AttributeEditorEnumListAttribute(attributeAccessor, inputId);
            this.attributeEditors[AttributeType.LocalDateAttribute] = (attributeAccessor, inputId, factoryEditor, waitAnimation) => new AttributeEditorLocalDateAttribute(attributeAccessor, inputId);
            this.attributeEditors[AttributeType.BooleanAttribute] = (attributeAccessor, inputId, factoryEditor, waitAnimation) => new AttributeEditorBooleanAttribute(attributeAccessor, inputId);
            this.attributeEditors[AttributeType.DoubleAttribute] = (attributeAccessor, inputId, factoryEditor, waitAnimation) => new AttributeEditorDoubleAttribute(attributeAccessor, inputId);
            this.attributeEditors[AttributeType.ByteAttribute] = (attributeAccessor, inputId, factoryEditor, waitAnimation) => new AttributeEditorByteAttribute(attributeAccessor, inputId);
            this.attributeEditors[AttributeType.FileContentAttribute] = (attributeAccessor, inputId, factoryEditor, waitAnimation) => new AttributeEditorFileContentAttribute(attributeAccessor, inputId);
            this.attributeEditors[AttributeType.StringListAttribute] = (attributeAccessor, inputId, factoryEditor, waitAnimation) => new AttributeEditorStringListAttribute(attributeAccessor, inputId);
            this.attributeEditors[AttributeType.FloatAttribute] = (attributeAccessor, inputId, factoryEditor, waitAnimation) => new AttributeEditorFloatAttribute(attributeAccessor, inputId);
            this.attributeEditors[AttributeType.FactoryAttribute] = (attributeAccessor, inputId, factoryEditor, waitAnimation) => new AttributeEditorFactoryAttribute(attributeAccessor, inputId, factoryEditor, waitAnimation);
            this.attributeEditors[AttributeType.FactoryListAttribute] = (attributeAccessor, inputId, factoryEditor, waitAnimation) => new AttributeEditorFactoryListAttribute(attributeAccessor, inputId, factoryEditor, waitAnimation);
            this.attributeEditors[AttributeType.FactoryPolymorphicAttribute] = (attributeAccessor, inputId, factoryEditor, waitAnimation) => new AttributeEditorFactoryAttribute(attributeAccessor, inputId, factoryEditor, waitAnimation);
            this.attributeEditors[AttributeType.FactoryPolymorphicListAttribute] = (attributeAccessor, inputId, factoryEditor, waitAnimation) => new AttributeEditorFactoryListAttribute(attributeAccessor, inputId, factoryEditor, waitAnimation);
            this.attributeEditors[AttributeType.FactoryViewAttribute] = (attributeAccessor, inputId, factoryEditor, waitAnimation) => new AttributeEditorFactoryViewAttribute(attributeAccessor, inputId, factoryEditor, waitAnimation);
            this.attributeEditors[AttributeType.FactoryViewListAttribute] = (attributeAccessor, inputId, factoryEditor, waitAnimation) => new AttributeEditorFactoryViewListAttribute(attributeAccessor, inputId, factoryEditor, waitAnimation);
        }
        else {
            this.attributeEditors = attributeEditors;
        }
    }
    create(attributeAccessor, inputId, factoryEditor, waitAnimation) {
        if (!this.attributeEditors[attributeAccessor.getAttributeMetadata().getType()]) {
            return new AttributeEditorFallback(attributeAccessor, inputId);
        }
        return this.attributeEditors[attributeAccessor.getAttributeMetadata().getType()](attributeAccessor, inputId, factoryEditor, waitAnimation);
    }
}
