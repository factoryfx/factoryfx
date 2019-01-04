//generated code don't edit manually
import { Data } from "../../../../../../../util/Data";
import { AttributeAccessor } from "../../../../../../../util/AttributeAccessor";
import { ExampleFactory } from "../../../../../../../config/de/factoryfx/factory/typescript/generator/data/ExampleFactory";
import { DataCreator } from "../../../../../../../util/DataCreator";
import { AttributeType } from "../../../../../../../util/AttributeType";
import { AttributeMetadata } from "../../../../../../../util/AttributeMetadata";

export abstract class ExampleFactoryGenerated  extends Data {

    public attribute: string;
    public ref: ExampleFactory;
    public refList: ExampleFactory[];
    public static readonly attributeMetadata: AttributeMetadata<string>= new AttributeMetadata<string>('labelEn\"\'\\','labelDe',AttributeType.StringAttribute);
    public static readonly refMetadata: AttributeMetadata<ExampleFactory>= new AttributeMetadata<ExampleFactory>('','',AttributeType.FactoryReferenceAttribute);
    public static readonly refListMetadata: AttributeMetadata<ExampleFactory[]>= new AttributeMetadata<ExampleFactory[]>('','',AttributeType.FactoryReferenceListAttribute);

    public attributeAccessor(): AttributeAccessor<string,ExampleFactoryGenerated>{
        return new AttributeAccessor<string,ExampleFactoryGenerated>(ExampleFactoryGenerated.attributeMetadata,this,"attribute");
    }

    public refAccessor(): AttributeAccessor<ExampleFactory,ExampleFactoryGenerated>{
        return new AttributeAccessor<ExampleFactory,ExampleFactoryGenerated>(ExampleFactoryGenerated.refMetadata,this,"ref");
    }

    public refListAccessor(): AttributeAccessor<ExampleFactory[],ExampleFactoryGenerated>{
        return new AttributeAccessor<ExampleFactory[],ExampleFactoryGenerated>(ExampleFactoryGenerated.refListMetadata,this,"refList");
    }

    protected mapValuesFromJson(json: any, idToDataMap: any, dataCreator: DataCreator){
        this.attribute=json.attribute.v;
        this.ref=<ExampleFactory>dataCreator.createData(json.ref.v,idToDataMap);
        this.refList=<ExampleFactory[]>dataCreator.createDataList(json.refList,idToDataMap);
    }

    protected mapValuesToJson(idToDataMap: any, result: any){
        result.attribute=this.mapAttributeValueToJson(this.attribute);
        result.ref=this.mapAttributeDataToJson(idToDataMap,this.ref);
        result.refList=this.mapAttributeDataListToJson(idToDataMap,this.refList);
    }

    protected collectChildrenRecursiveIntern(idToDataMap: any){
        this.collectDataChildren(this.ref,idToDataMap);
        this.collectDataArrayChildren(this.refList,idToDataMap);
    }

    protected listAttributeAccessor(): AttributeAccessor<any,ExampleFactoryGenerated>[]{
        let result: AttributeAccessor<any,ExampleFactoryGenerated>[]=[];
        result.push(this.attributeAccessor());
        result.push(this.refAccessor());
        result.push(this.refListAccessor());
        return result;
    }

}