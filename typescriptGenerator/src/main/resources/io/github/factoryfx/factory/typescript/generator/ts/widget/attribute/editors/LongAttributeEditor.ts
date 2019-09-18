import {AttributeAccessor} from "../../../AttributeAccessor";
import {NumberBaseAttributeEditor} from "./NumberBaseAttributeEditor";

export class LongAttributeEditor extends NumberBaseAttributeEditor{


    constructor(protected attributeAccessor: AttributeAccessor<any>, protected inputId: string) {
        super(attributeAccessor,inputId);
    }

    protected additionalInputSetup(): any {
        this.input.type="text";
    }

    public bindModel(): any {
        this.renderOnce();
        this.input.value=this.attributeAccessor.getValue();
        this.input.oninput=(e) => {
            try {
                let value = BigInt(this.input.value);
                if (value<9223372036854775807 && value>-9223372036854775807){
                    this.attributeAccessor.setValue(value);
                    this.input.setCustomValidity("");
                } else {
                    this.input.setCustomValidity("Number not in java Long range");
                }
            } catch (error) {
                this.input.setCustomValidity("Not a Number");
            }
        };
        this.input.required=!this.attributeAccessor.getAttributeMetadata().nullable();
    }

}