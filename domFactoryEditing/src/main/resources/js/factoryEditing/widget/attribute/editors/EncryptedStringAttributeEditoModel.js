//generated code don't edit manually
import { EncryptedStringAttributeEditor } from "./EncryptedStringAttributeEditor";
import { AttributeAccessorValue } from "../../../base/AttributeAccessorValue";
import { StringValue } from "../../../base/StringValue";
import { AttributeEditorModel } from "../AttributeEditorModel";
export class EncryptedStringAttributeEditorModel extends AttributeEditorModel {
    constructor(factoryEditorModel, httpClient) {
        super((model) => { return new EncryptedStringAttributeEditor(this, this.factoryEditorModel, this.httpClient); });
        this.factoryEditorModel = factoryEditorModel;
        this.httpClient = httpClient;
        this.attributeAccessor = new AttributeAccessorValue();
        this.inputId = new StringValue();
        this.key = new StringValue();
    }
}
