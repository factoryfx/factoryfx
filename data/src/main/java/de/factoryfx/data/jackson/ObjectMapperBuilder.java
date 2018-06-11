package de.factoryfx.data.jackson;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;

public class ObjectMapperBuilder {
    private static SimpleObjectMapper simpleObjectMapper;

    public static SimpleObjectMapper build() {
        if (simpleObjectMapper == null) {
            simpleObjectMapper = buildNew();
        }
        return simpleObjectMapper;
    }

    public static ObjectMapper buildNewObjectMapper() {
        return setupMapper();
    }

    public static SimpleObjectMapper buildNew() {
        return new SimpleObjectMapper(setupMapper());
    }

    private static ObjectMapper setupMapper() {
        JsonFactory jsonFactory = new JsonFactory();

        // disable the thread local to prevent memory leak
//        jsonFactory.disable(JsonFactory.Feature.INTERN_FIELD_NAMES);
//        new com.fasterxml.jackson.dataformat.smile.SmileFactory();
        ObjectMapper objectMapper = new ObjectMapper(jsonFactory);



        objectMapper.enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING);
        objectMapper.enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING);
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);


        objectMapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
        objectMapper.registerModule(new Jdk8Module());
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE);
        objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);


//        objectMapper.setVisibility(objectMapper.getSerializationConfig().getDefaultVisibilityChecker()
//                .withFieldVisibility(JsonAutoDetect.Visibility.NONE)
//                .withGetterVisibility(JsonAutoDetect.Visibility.NONE)
//                .withSetterVisibility(JsonAutoDetect.Visibility.NONE)
//                .withCreatorVisibility(JsonAutoDetect.Visibility.NONE));

        objectMapper.setDefaultMergeable(true); // global default, merging

//        objectMapper.configOverride(EnumAttribute.class).setMergeable(false);



//        objectMapper.configOverride(Attribute).setSetterInfo(V)
//        objectMapper.configOverride(Object.class) // prevent merging of values with type `Object`
//                .setMergeable(false);

//        objectMapper.configure(DeserializationFeature.UNWRAP_ROOT_VALUE, true);

//        objectMapper.setDefaultMergeable(true);

//        objectMapper.enable(MapperFeature.IGNORE_MERGE_FOR_UNMERGEABLE);


//        SerializerFactory serializerFactory = BeanSerializerFactory.instance
//                .withSerializerModifier(new MyBeanSerializerModifier());
//
//
//        DeserializerFactory deserializerFactory = BeanDeserializerFactory.instance
//                .
//                .withDeserializerModifier(new MyBeanDeserializerModifier());
//
//        objectMapper.setSerializerFactory(serializerFactory);
//        objectMapper.setDeserializerProvider(new StdDeserializerProvider(deserializerFactory));
//
//

//        objectMapper.disable(SerializationFeature.FAIL_ON_UNWRAPPED_TYPE_IDENTIFIERS);


//        objectMapper.registerModule(new MyModule());

        return objectMapper;
    }
//
//    public static class MyModule extends SimpleModule
//    {
//        public MyModule() {
//            super("ModuleName", new Version(0,0,1,null));
//
//            setDeserializers(new SimpleDeserializers());
//
//            setDeserializerModifier(new BeanDeserializerModifier(){
//
//                //add Null.SKIP Annotation to all Data attributes
//                public JsonDeserializer<?> modifyDeserializer(DeserializationConfig config, BeanDescription beanDesc, JsonDeserializer<?> deserializer) {
//                    if (deserializer instanceof BeanDeserializer){
//
//
//
//
////                        List<BeanPropertyDefinition> properties = beanDesc.findProperties();
////
////
////                        for(BeanPropertyDefinition property: properties) {
////
////                            try {
////                                PropertyMetadata newPropertyMetadata = property.getMetadata();
////                                Field metadata = newPropertyMetadata.getClass().getDeclaredField("_valueNulls");
////                                metadata.setAccessible(true);
////                                metadata.set(newPropertyMetadata, Nulls.SKIP);
////
////                                Field mergeInfo = newPropertyMetadata.getClass().getDeclaredField("_mergeInfo");
////                                mergeInfo.setAccessible(true);
//////                                metadata.set(newPropertyMetadata, new AnnotatedParameter());
////
////                            } catch (IllegalAccessException | NoSuchFieldException e) {
////                                throw new RuntimeException(e);
////                            }
////                        }
//
//
//                        return new CustomBeanDeserializer((BeanDeserializer)deserializer);
//                    }
//                    return deserializer;
//                }
//
//            });
//        }
//    }
//
//
//    public static class CustomBeanDeserializer extends BeanDeserializer{
//
//        protected CustomBeanDeserializer(BeanDeserializerBase src) {
//            super(src);
//        }
//
//        @Override
//        public Object deserialize(JsonParser p, DeserializationContext ctxt, Object bean) throws IOException {
//            return super.deserialize(p, ctxt, bean);
//        }
//
//        @Override
//        public Object deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
//            ObjectMapper mapper = (ObjectMapper)jp.getCodec();
//
//
//
//            final Data bean1 = (Data)_valueInstantiator.createUsingDefault(ctxt);
//            Data bean = bean1.newInstance();
//            HashMap<String,Attribute> attributeMap = new HashMap<>();
//            bean.internal().visitAttributesFlat(attributeMap::put);
//
//            String propName = jp.getCurrentName();
//            do {
//                jp.nextToken();
//                Attribute attribute = attributeMap.get(propName);
//                if (attribute instanceof StringAttribute){
//                    ((StringAttribute)attribute).set(jp.getValueAsString());
//                }
//                if (attribute instanceof ReferenceAttribute){
//                    ((ReferenceAttribute)attribute).set((Data)ctxt.readValue(jp.getCodec().readTree(jp).traverse(), ((ReferenceAttribute)attribute).internal_getReferenceClass()));
//                }
//
//
//            } while ((propName = jp.nextFieldName()) != null);
//            return bean;
//
//
//            //            Property property = mapper.readValue(jn.get("propertyValue").toString(), Property.class))
//
////            TreeNode treeNode = p.getCodec().readTree(p);
////            treeNode.fieldNames().forEachRemaining(new Consumer<String>() {
////                @Override
////                public void accept(String s) {
////                    System.out.println(s);
////                }
////            });
////            treeNode.
//
//
////
//////            p.getCodec().readTree(p);
//////            ctxt.gett
////            final Data bean = (Data)_valueInstantiator.createUsingDefault(ctxt);
////
////
////            HashMap<String,Attribute> attributeMap = new HashMap<>();
////            bean.internal().visitAttributesFlat(attributeMap::put);
////
////
////
////            String propName = p.getCurrentName();
////            do {
////                p.nextToken();
////                System.out.println(propName);
////
////                if (propName.equals("id")){
////                    bean.setId(p.getValueAsString());
////                } else {
////                    Attribute attribute = attributeMap.get(propName);
////                    if (attribute instanceof StringAttribute){
////                        ((StringAttribute)attribute).set(p.getValueAsString());
////                    }
////                    if (attribute instanceof ReferenceAttribute){
////                        ((ReferenceAttribute)attribute).set((Data)ctxt.readValue(p.getCodec().readTree(p).traverse(), ((ReferenceAttribute)attribute).internal_getReferenceClass()));
////                    }
////
////                }
////
////
//////                if (p.isExpectedStartObjectToken()) {
//////                    if (_vanillaProcessing) {
//////                        return deserialize(p, ctxt);
//////                    }
//////                    // 23-Sep-2015, tatu: This is wrong at some many levels, but for now... it is
//////                    //    what it is, including "expected behavior".
//////                    p.nextToken();
//////                    if (_objectIdReader != null) {
//////                        return deserializeWithObjectId(p, ctxt);
//////                    }
//////                    return deserializeFromObject(p, ctxt);
//////                }
////
//////                if (p.isExpectedStartObjectToken()){
//////                    System.out.println(deserialize(p.readValueAsTree().traverse(),ctxt));
//////                } else {
//////                    System.out.println(p.getValueAsString());
//////                }
////
//////                SettableBeanProperty prop = _beanProperties.find(propName);
//////
//////                if (prop != null) { // normal case
//////                    try {
//////                        prop.deserializeAndSet(p, ctxt, bean);
//////                    } catch (Exception e) {
//////                        wrapAndThrow(e, bean, propName, ctxt);
//////                    }
//////                    continue;
//////                }
//////                handleUnknownVanilla(p, ctxt, bean, propName);
////            } while ((propName = p.nextFieldName()) != null);
////
////
//////            bean.internal().visitAttributesFlat(new Data.AttributeVisitor() {
//////                @Override
//////                public void accept(String attributeVariableName, Attribute<?, ?> attribute) {
//////                    try {
//////                        p.getCodec().readTree(p).get(attributeVariableName);
//////                        //attribute.set( deserialize(p.getCodec().readTree(p).get(attributeVariableName).traverse(),ctxt));
//////                    } catch (IOException e) {
//////                        throw new RuntimeException(e);
//////                    }
//////                }
//////            });
//
////            return null; //super.deserialize(p, ctxt);
//        }
//    }

}
