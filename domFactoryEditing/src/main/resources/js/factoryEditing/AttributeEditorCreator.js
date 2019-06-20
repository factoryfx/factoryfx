import { AttributeType } from "./AttributeType";
import { AttributeEditorStringAttribute } from "./AttributeEditorStringAttribute";
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
export class AttributeEditorCreator {
    constructor(attributeEditors) {
        if (!attributeEditors) {
            this.attributeEditors = [];
            this.attributeEditors[AttributeType.StringAttribute] = (attributeAccessor, inputId, factoryEditor) => new AttributeEditorStringAttribute(attributeAccessor, inputId);
            this.attributeEditors[AttributeType.IntegerAttribute] = (attributeAccessor, inputId, factoryEditor) => new AttributeEditorIntegerAttribute(attributeAccessor, inputId);
            this.attributeEditors[AttributeType.LongAttribute] = (attributeAccessor, inputId, factoryEditor) => new AttributeEditorLongAttribute(attributeAccessor, inputId);
            this.attributeEditors[AttributeType.EnumAttribute] = (attributeAccessor, inputId, factoryEditor) => new AttributeEditorEnumAttribute(attributeAccessor, inputId);
            this.attributeEditors[AttributeType.EnumListAttribute] = (attributeAccessor, inputId, factoryEditor) => new AttributeEditorEnumListAttribute(attributeAccessor, inputId);
            this.attributeEditors[AttributeType.LocalDateAttribute] = (attributeAccessor, inputId, factoryEditor) => new AttributeEditorLocalDateAttribute(attributeAccessor, inputId);
            this.attributeEditors[AttributeType.BooleanAttribute] = (attributeAccessor, inputId, factoryEditor) => new AttributeEditorBooleanAttribute(attributeAccessor, inputId);
            this.attributeEditors[AttributeType.DoubleAttribute] = (attributeAccessor, inputId, factoryEditor) => new AttributeEditorDoubleAttribute(attributeAccessor, inputId);
            this.attributeEditors[AttributeType.FactoryAttribute] = (attributeAccessor, inputId, factoryEditor) => new AttributeEditorFactoryAttribute(attributeAccessor, inputId, factoryEditor);
            this.attributeEditors[AttributeType.FactoryListAttribute] = (attributeAccessor, inputId, factoryEditor) => new AttributeEditorFactoryListAttribute(attributeAccessor, inputId, factoryEditor);
            this.attributeEditors[AttributeType.FactoryPolymorphicAttribute] = (attributeAccessor, inputId, factoryEditor) => new AttributeEditorFactoryAttribute(attributeAccessor, inputId, factoryEditor);
            this.attributeEditors[AttributeType.FactoryPolymorphicListAttribute] = (attributeAccessor, inputId, factoryEditor) => new AttributeEditorFactoryListAttribute(attributeAccessor, inputId, factoryEditor);
        }
        else {
            this.attributeEditors = attributeEditors;
        }
    }
    create(attributeAccessor, inputId, factoryEditor) {
        if (!this.attributeEditors[attributeAccessor.getAttributeMetadata().getType()]) {
            return new AttributeEditorFallback(attributeAccessor, inputId);
        }
        return this.attributeEditors[attributeAccessor.getAttributeMetadata().getType()](attributeAccessor, inputId, factoryEditor);
    }
}
