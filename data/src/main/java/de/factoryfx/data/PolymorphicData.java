package de.factoryfx.data;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

//
// usage example
// DataReferenceAttribute<PolymorphicData> attribute = new DataReferenceAttribute<PolymorphicData>()
//
// minimal class don't work
@JsonTypeInfo(use=JsonTypeInfo.Id.CLASS, include=JsonTypeInfo.As.PROPERTY, property="@class")
public class PolymorphicData extends Data {

}
