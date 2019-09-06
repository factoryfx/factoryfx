import {AttributeAccessor} from "../../../AttributeAccessor";
import {AttributeEditorWidget} from "../AttributeEditorWidget";

export class StringAttributeEditor extends AttributeEditorWidget{
    private input: HTMLInputElement= document.createElement("input");
    constructor(protected attributeAccessor: AttributeAccessor<any>, protected inputId: string) {
        super(attributeAccessor,inputId);
    }

    protected renderAttribute(): HTMLElement{
        this.input.id=this.inputId.toString();
        this.input.className="form-control";
        this.input.type="text";

        this.input.required=!this.attributeAccessor.getAttributeMetadata().nullable();
        return this.input;
    }

    protected bindAttribute(): any {
        this.input.value=this.attributeAccessor.getValue();
        this.input.oninput=(e) => {
            this.attributeAccessor.setValue(this.input.value);
        };
        this.input.required=!this.attributeAccessor.getAttributeMetadata().nullable();
    }


}