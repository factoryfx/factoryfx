import {AttributeAccessor} from "../../../AttributeAccessor";
import {AttributeEditorWidget} from "../AttributeEditorWidget";

export abstract class NumberBaseAttributeEditor extends AttributeEditorWidget{

    protected input: HTMLInputElement= document.createElement("input");
    constructor(protected attributeAccessor: AttributeAccessor<any>, protected inputId: string) {
        super(attributeAccessor,inputId);
        this.input.id=this.inputId.toString();
        this.input.className="form-control";
        this.input.type="number";
        this.additionalInputSetup();
    }

    protected render(): HTMLElement{
        return this.input;
    }

    protected abstract additionalInputSetup(): any;

    public bindModel(): any {
        this.renderOnce();
        this.input.valueAsNumber=this.attributeAccessor.getValue();
        this.input.oninput=(e) => {
            this.attributeAccessor.setValue(this.input.valueAsNumber);
        };
        this.input.required=!this.attributeAccessor.getAttributeMetadata().nullable();
    }



}