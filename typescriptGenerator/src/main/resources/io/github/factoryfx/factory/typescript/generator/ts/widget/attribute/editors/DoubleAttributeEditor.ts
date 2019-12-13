import {AttributeAccessor} from "../../../AttributeAccessor";
import {NumberBaseAttributeEditor} from "./NumberBaseAttributeEditor";

export class DoubleAttributeEditor extends NumberBaseAttributeEditor{

    constructor(protected attributeAccessor: AttributeAccessor<any>, protected inputId: string) {
        super(attributeAccessor,inputId);
    }

    protected additionalInputSetup(): any {
        this.input.step='any';
    }





}