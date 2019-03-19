//generated code don't edit manually
export enum ExampleEnum  {

VALUE1="VALUE1",
VALUE2="VALUE2"

}
export namespace ExampleEnum {
    export function fromJson(json: string): ExampleEnum{
        return ExampleEnum[json];
    }
    export function toJson(value: ExampleEnum): string{
        if (value) return value.toString();
    }
}
