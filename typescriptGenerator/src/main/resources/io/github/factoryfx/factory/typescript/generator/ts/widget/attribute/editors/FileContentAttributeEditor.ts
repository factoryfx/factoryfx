import {AttributeAccessor} from "../../../AttributeAccessor";
import {AttributeEditorWidget} from "../AttributeEditorWidget";

export class FileContentAttributeEditor extends AttributeEditorWidget{
    private textarea: HTMLTextAreaElement;

    constructor(protected attributeAccessor: AttributeAccessor<any>, protected inputId: string) {
        super(attributeAccessor,inputId);
        this.textarea = document.createElement("textarea");
        this.textarea.className="form-control";
    }

    protected renderAttribute(): HTMLElement{
        let content: HTMLDivElement= document.createElement("div");
        let input: HTMLInputElement= document.createElement("input");
        input.id=this.inputId.toString();
        input.className="form-control-file";
        input.type="file";

        // input.value=this.attributeAccessor.getValue();  TODO how show base 64
        input.oninput=(e) => {
            if (input.files){
                let reader:FileReader = new FileReader();
                reader.readAsDataURL(input.files[0]);
                reader.onload = () => {
                    let result: string= reader.result as string;
                    this.attributeAccessor.setValue(result.split(',')[1]);
                    this.bindValue();
                };
            }


        };
        this.bindValue();

        content.appendChild(this.textarea);
        content.appendChild(input);
        return content;
    }

    bindValue(){
        let value = this.attributeAccessor.getValue();
        this.textarea.value= value;
        // this.textarea.readOnly=true; doesn't work width required
        this.textarea.required=!this.attributeAccessor.getAttributeMetadata().nullable();
    }


}