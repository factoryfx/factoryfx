import {AttributeAccessor} from "../../../AttributeAccessor";
import {NumberBaseAttributeEditor} from "./NumberBaseAttributeEditor";

export class IntegerAttributeEditor extends NumberBaseAttributeEditor {

    constructor(protected attributeAccessor: AttributeAccessor<any>, protected inputId: string) {
        super(attributeAccessor, inputId);
    }

    protected additionalInputSetup(): any {
        this.input.max = "2147483647";
        this.input.min = "-2147483648";
    }

}

