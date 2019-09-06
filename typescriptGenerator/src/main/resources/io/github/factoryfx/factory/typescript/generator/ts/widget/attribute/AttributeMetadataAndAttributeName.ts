import {AttributeMetadata} from "../../AttributeMetadata";


export class AttributeMetadataAndAttributeName {
    public readonly attributeMetadata: AttributeMetadata<any>;
    public readonly attributeName: string;

    constructor(attributeMetadata: AttributeMetadata<any>, attributeName: string) {
        this.attributeMetadata = attributeMetadata;
        this.attributeName = attributeName;
    }
}