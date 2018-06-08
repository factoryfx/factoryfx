package de.factoryfx.data.attribute.types;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.std.StdNodeBasedDeserializer;
import com.fasterxml.jackson.databind.type.SimpleType;

import java.io.IOException;

public class StringAttributeDeserialzer extends StdNodeBasedDeserializer<StringAttribute> {


    protected StringAttributeDeserialzer() {
        super(SimpleType.constructUnsafe(StringAttribute.class));
    }

    @Override
    public StringAttribute convert(JsonNode root, DeserializationContext ctxt) throws IOException {
        StringAttribute stringAttribute = new StringAttribute();
        stringAttribute.set(root.get("value").asText());
        return stringAttribute;
    }

    //
//    @Override
//    public Data deserialize(JsonParser jp, DeserializationContext ctxt)
//            throws IOException, JsonProcessingException {
//        ObjectMapper mapper = (ObjectMapper)jp.getCodec();
//        ObjectNode node = mapper.readTree(jp);
//        String value = node.get("@class").asText();
//
//        mapper.getTypeFactory().
////        JsonLocation startLocation = jp.getCurrentLocation();
////
////        JsonNode node = jp.getCodec().readTree(jp);
////        int id = (Integer) ((IntNode) node.get("id")).numberValue();
////        String itemName = node.get("itemName").asText();
////        int userId = (Integer) ((IntNode) node.get("createdBy")).numberValue();
//        return null;//;new Item(id, itemName, new User(userId, null));
//    }
}
