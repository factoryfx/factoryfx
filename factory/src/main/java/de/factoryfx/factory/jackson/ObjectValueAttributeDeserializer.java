package de.factoryfx.factory.jackson;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import de.factoryfx.factory.attribute.util.ObjectValueAttribute;

/** ignore value, @Ignore annotation doesn't work therefore this custom Deserializer*/
public class ObjectValueAttributeDeserializer extends JsonDeserializer {
    @Override
    public ObjectValueAttribute deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        return new ObjectValueAttribute(null);
    }
}
