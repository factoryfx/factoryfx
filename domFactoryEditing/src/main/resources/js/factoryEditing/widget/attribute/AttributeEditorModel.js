//generated code don't edit manually
import { WidgetModel } from "../../base/WidgetModel";
import { StringValue } from "../../base/StringValue";
import { AttributeAccessorValue } from "../../base/AttributeAccessorValue";
export class AttributeEditorModel extends WidgetModel {
    constructor(creator) {
        super();
        this.creator = creator;
        this.attributeAccessor = new AttributeAccessorValue();
        this.inputId = new StringValue();
    }
    createWidget() {
        return this.creator(this);
    }
}
