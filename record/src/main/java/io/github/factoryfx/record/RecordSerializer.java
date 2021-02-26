package io.github.factoryfx.record;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import io.github.factoryfx.factory.AttributeVisitor;
import io.github.factoryfx.factory.attribute.Attribute;
import io.github.factoryfx.factory.attribute.ImmutableValueAttribute;
import io.github.factoryfx.factory.attribute.types.ObjectValueAttribute;
import io.github.factoryfx.factory.metadata.AttributeMetadata;

import java.io.IOException;

public class RecordSerializer extends JsonSerializer<RecordFactory<?,?,?>> {


    @Override
    public void serialize(RecordFactory value, JsonGenerator gen, SerializerProvider serializers) throws IOException {

    }

    @Override
    public void serializeWithType(RecordFactory value, JsonGenerator gen, SerializerProvider serializers, TypeSerializer typeSer) throws IOException {
        gen.writeStartObject();
        gen.writeStringField("@class",value.getClass().getName());
        gen.writeStringField("@classRecord",value.dep().getClass().getName());
        gen.writeStringField("id",value.getId().toString());
        value.internal().visitAttributesFlat((attributeMetadata, attribute) -> {
            try {
                if ((attribute instanceof ImmutableValueAttribute)){
                    if (attribute.get()!=null){
                        gen.writeObjectField(attributeMetadata.attributeVariableName,attribute);
                    }
                }  else {
                    gen.writeObjectField(attributeMetadata.attributeVariableName,attribute);
                }


//                gen.writeObjectField("v",attribute);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        });
        gen.writeEndObject();
    }
}
