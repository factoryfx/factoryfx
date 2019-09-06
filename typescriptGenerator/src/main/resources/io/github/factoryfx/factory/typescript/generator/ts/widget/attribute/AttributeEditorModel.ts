import {WidgetModel} from "../../base/WidgetModel";
import {StringValue} from "../../base/StringValue";
import {AttributeAccessorValue} from "../../base/AttributeAccessorValue";
import {AttributeEditorWidget} from "./AttributeEditorWidget";

export class AttributeEditorModel extends WidgetModel<AttributeEditorWidget> {

    public readonly attributeAccessor: AttributeAccessorValue = new AttributeAccessorValue();
    public readonly inputId: StringValue = new StringValue();

    constructor(private creator: (model: AttributeEditorModel)=>AttributeEditorWidget){
        super();
    }

    protected createWidget(): AttributeEditorWidget {
        return this.creator(this);
    }


}