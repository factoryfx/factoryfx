import {WidgetModel} from "../../../base/WidgetModel";
import {EncryptedStringAttributeEditor} from "./EncryptedStringAttributeEditor";
import {AttributeAccessorValue} from "../../../base/AttributeAccessorValue";
import {StringValue} from "../../../base/StringValue";
import {AttributeEditorModel} from "../AttributeEditorModel";
import {FactoryEditorModel} from "../../factoryeditor/FactoryEditorModel";
import {HttpClient} from "../../../HttpClient";


export class EncryptedStringAttributeEditorModel extends AttributeEditorModel{

    public readonly attributeAccessor: AttributeAccessorValue = new AttributeAccessorValue();
    public readonly inputId: StringValue = new StringValue();
    public readonly key: StringValue = new StringValue();


    constructor(private factoryEditorModel: FactoryEditorModel, private httpClient: HttpClient){
        super((model)=>{return new EncryptedStringAttributeEditor(this, this.factoryEditorModel, this.httpClient)});
    }





}