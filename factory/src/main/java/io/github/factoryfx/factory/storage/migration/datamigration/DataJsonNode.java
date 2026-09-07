package io.github.factoryfx.factory.storage.migration.datamigration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;

import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.jackson.SimpleObjectMapper;
import io.github.factoryfx.factory.storage.migration.metadata.AttributeStorageMetadata;
import io.github.factoryfx.factory.storage.migration.metadata.DataStorageMetadata;
import io.github.factoryfx.factory.storage.migration.metadata.DataStorageMetadataDictionary;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class DataJsonNode {
    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(DataJsonNode.class);

    private final ObjectNode jsonNode;


    public DataJsonNode(ObjectNode jsonNode) {
        this.jsonNode = jsonNode;
    }

    public void removeAttribute(String name){
        jsonNode.remove(name);
    }

    public String getDataClassName(){
        return jsonNode.get("@class").textValue();
    }

    public boolean match(String dataClassNameFullQualified){
        return getDataClassName().equals(dataClassNameFullQualified);
    }

    public void renameAttribute(String previousAttributeName, String newAttributeName) {
        jsonNode.set(newAttributeName, jsonNode.get(previousAttributeName));
        jsonNode.remove(previousAttributeName);
    }

    public void renameClass(Class<? extends FactoryBase<?,?>> newDataClass) {
        jsonNode.set("@class",new TextNode(newDataClass.getName()));
    }

    public DataJsonNode getChild(String attributeName) {
        JsonNode attribute = jsonNode.get(attributeName);
        if (attribute==null){
            return null;
        }
        return new DataJsonNode((ObjectNode)attribute.get("v"));
    }

    public DataJsonNode getChild(String attributeName, int index) {
        if (!jsonNode.get(attributeName).isArray()){
            throw new IllegalArgumentException("is not a reflist attribute: "+attributeName);
        }
        return new DataJsonNode((ObjectNode)jsonNode.get(attributeName).get(index));
    }

    public JsonNode getAttributeValue(String attribute) {
        if (jsonNode.get(attribute)==null){
            return null;
        }
        if (jsonNode.get(attribute).isArray()){
            return jsonNode.get(attribute);
        }
        return jsonNode.get(attribute).get("v");
    }

    public void setAttributeValue(String attribute, JsonNode jsonNode) {
        try {
            if (jsonNode == null) {
                if (this.jsonNode.get(attribute) instanceof ObjectNode) {
                    ((ObjectNode) this.jsonNode.get(attribute)).remove("v");
                }
            }
            if (this.jsonNode.get(attribute) instanceof ObjectNode) {
                ObjectNode objectNode = JsonNodeFactory.instance.objectNode();
                this.jsonNode.set(attribute, objectNode);
                objectNode.set("v", jsonNode);
            } else if (this.jsonNode.get(attribute) instanceof ArrayNode) {
                this.jsonNode.set(attribute, jsonNode);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * set an attribute value forcing the json shape of the target attribute type, independent of the previously stored shape.<br>
     * (list attributes are stored as bare json array, single value attributes wrapped as {"v": ...})
     * @param attribute attribute variable name
     * @param value unwrapped value, null clears the attribute (empty array for list attributes)
     * @param listShape true if the target attribute is a list attribute
     */
    public void setAttributeValueTargetShape(String attribute, JsonNode value, boolean listShape) {
        if (listShape) {
            this.jsonNode.set(attribute, value == null ? JsonNodeFactory.instance.arrayNode() : value);
        } else {
            ObjectNode objectNode = JsonNodeFactory.instance.objectNode();
            objectNode.set("v", value);
            this.jsonNode.set(attribute, objectNode);
        }
    }

    public <V> V getAttributeValue(String attributeName, Class<V> valueClass, SimpleObjectMapper simpleObjectMapper) {
        JsonNode attributeValue = getAttributeValue(attributeName);
        if (attributeValue==null){
            return null;
        }
        return simpleObjectMapper.treeToValue(attributeValue, valueClass);
    }

    public <V> V getArrayAttributeValue(String attributeName, Class<V> valueClass, SimpleObjectMapper simpleObjectMapper, int index) {
        ArrayNode attributeValue = (ArrayNode) getAttributeValue(attributeName);
        return simpleObjectMapper.treeToValue(attributeValue.get(index), valueClass);
    }

    public String getAttributeIdValue(String attributeName) {
        return jsonNode.get(attributeName).get("v").asText();
    }

    private boolean isData(JsonNode jsonNode){
        if (jsonNode==null){
            return false;
        }
        if (jsonNode.fieldNames().hasNext()){
            String fieldName = jsonNode.fieldNames().next();
            return "@class".equals(fieldName);
        }
        return false;
    }

    public boolean isData(){
        return isData(this.jsonNode);
    }

    private void collectChildrenDeep(List<DataJsonNode> dataJsonNodes){
        for (JsonNode element : jsonNode) {
            if (element.isArray()) {
                for (JsonNode arrayElement : element) {
                    if (isData(arrayElement)) {
                        DataJsonNode child = new DataJsonNode((ObjectNode) arrayElement);
                        dataJsonNodes.add(child);
                        child.collectChildrenDeep(dataJsonNodes);
                    }
                }
            } else {
                if (isData(element.get("v"))) {
                    DataJsonNode child = new DataJsonNode((ObjectNode) element.get("v"));
                    dataJsonNodes.add(child);
                    child.collectChildrenDeep(dataJsonNodes);
                }

            }
        }
    }

    /**
     *  get children including himself
     * @return children
     */
    public List<DataJsonNode> collectChildrenFromRoot(){
        List<DataJsonNode> dataJsonNode = new ArrayList<>();
        dataJsonNode.add(this);
        this.collectChildrenDeep(dataJsonNode);
        return dataJsonNode;
    }


    /**
     * reset the ids, (first occurrence is the object following are id references)
     * @param collected for recursion
     */
    private void replaceDuplicateFactoriesWidthIdDeep(HashSet<String> collected){
            this.visitAttributes((value, jsonNodeConsumer) -> {
                if (isData(value)) {
                    DataJsonNode child = new DataJsonNode((ObjectNode) value);
                    if (collected.add(child.getId())) {
                        child.replaceDuplicateFactoriesWidthIdDeep(collected);
                    } else {
                        jsonNodeConsumer.accept(new TextNode(child.getId()));
                    }
                }
            });
    }

    /**
     * replace all id refs with object
     * result is json without ids
     * @param idToDataJsonNode to resolve id
     */
    private void replaceIdRefsWidthFactoriesDeep(Map<String,DataJsonNode> idToDataJsonNode){
        this.visitAttributes((value, jsonNodeConsumer) -> {
            if (value.isTextual() && idToDataJsonNode.containsKey(value.asText())){
                jsonNodeConsumer.accept(idToDataJsonNode.get(value.asText()).jsonNode);
            }
            if (isData(value)){
                new DataJsonNode((ObjectNode) value).replaceIdRefsWidthFactoriesDeep(idToDataJsonNode);
            }
        });
    }


    //TODO return UUID
    public String getId() {
        return jsonNode.get("id").asText();
    }

    public <D> D asData(Class<D> valueClass, SimpleObjectMapper simpleObjectMapper) {
        return simpleObjectMapper.treeToValue(jsonNode, valueClass);
    }

    public List<String> getAttributes(){
        ArrayList<String> result = new ArrayList<>();

        for (Map.Entry<String, JsonNode> element : jsonNode.properties()) {
            if (element.getValue().isObject()) {
                result.add(element.getKey());
            }
            if (element.getValue().isArray()) {
                result.add(element.getKey());
            }
        }
        return result;
    }

    public Map<String,DataJsonNode> collectChildrenMapFromRoot() {
        HashMap<String, DataJsonNode> result = new HashMap<>();
        for (DataJsonNode dataJsonNode : collectChildrenFromRoot()) {
            result.put(dataJsonNode.getId(),dataJsonNode);
        }
        return result;

    }

    Map<String, DataJsonNode> cache;
    public Map<String,DataJsonNode> collectChildrenMapFromRootCached() {
        if (cache==null){
            cache=collectChildrenMapFromRoot();
        }
        return cache;
    }

    public void applyRemovedAttribute(DataStorageMetadataDictionary dataStorageMetadataDictionary){
        for (String attributeVariableName : this.getAttributes()) {
            if (dataStorageMetadataDictionary.isRemovedAttribute(getDataClassName(),attributeVariableName)){
                this.jsonNode.remove(attributeVariableName);
            }
        }
    }

    public void applyRetypedAttribute(DataStorageMetadataDictionary dataStorageMetadataDictionary){
        for (String attributeVariableName : this.getAttributes()) {
            if (dataStorageMetadataDictionary.isRetypedAttribute(getDataClassName(),attributeVariableName)){
                AttributeStorageMetadata attributeStorageMetadata = dataStorageMetadataDictionary.getAttributeStorageMetadata(getDataClassName(),attributeVariableName);
                JsonNode converted = AutomaticRetypeConversion.convert(this.getAttributeValue(attributeVariableName),getDataClassName(),attributeStorageMetadata);
                this.setAttributeValueTargetShape(attributeVariableName,converted,attributeStorageMetadata.isRetypedToList());
            }
        }
    }

    public void applyRemovedClasses(DataStorageMetadataDictionary dataStorageMetadataDictionary) {
        this.visitAttributes((jsonNode, jsonNodeConsumer) -> {
            if (isData(jsonNode)) {
                DataStorageMetadata dataStorageMetadata = dataStorageMetadataDictionary.getDataStorageMetadata(new DataJsonNode((ObjectNode) jsonNode).getDataClassName());
                if (dataStorageMetadata != null && dataStorageMetadata.isRemovedClass()) {
                    jsonNodeConsumer.accept(null);
                }
            }
        });
    }


    /**
     * relocate factory definitions out of parts of the json that the removal handling will drop (attributes removed
     * from the current model and factories of removed classes).<br>
     * References are serialized first-occurrence-defines ({@code JsonIdentityInfo}): the first occurrence is the full
     * object, all following occurrences are id references. When the first occurrence sits inside a removed attribute,
     * deserializing &ndash; or roundtripping through the current factory classes, e.g. inside a
     * {@link io.github.factoryfx.factory.storage.migration.ConfigurationPatch} &ndash; would silently drop the
     * definition and leave the remaining id references dangling. This moves each such definition to its first
     * remaining occurrence outside the removed parts, so removing an attribute or class no longer loses factories
     * that are still referenced. Definitions that are not referenced anywhere else are left in place (they are
     * dropped with the removed part, with a warning).<br>
     * Idempotent and a cheap no-op when no removed part contains a factory definition. Must be called on the root
     * node with a dictionary that is already marked ({@code markRemovedAttributes}/{@code markRemovedClasses}).
     * @param dictionary stored metadata dictionary with removal marks
     */
    public void relocateDefinitionsFromRemovedParts(DataStorageMetadataDictionary dictionary) {
        List<DoomedDefinition> doomedDefinitions = new ArrayList<>();
        collectDoomedDefinitions(this.jsonNode, dictionary, false, doomedDefinitions);
        for (DoomedDefinition doomed : doomedDefinitions) {
            switch (placeAtFirstRemainingOccurrence(this.jsonNode, dictionary, doomed)) {
                case PLACED -> {
                    doomed.replaceWith.accept(TextNode.valueOf(doomed.id));
                    logger.info("relocated factory {} ({}) out of a removed attribute/class to its first remaining reference", doomed.id, doomed.className);
                }
                case NOT_FOUND ->
                    logger.warn("factory {} ({}) is stored only inside a removed attribute/class and not referenced anywhere else, it is dropped", doomed.id, doomed.className);
                case ALREADY_PLACED -> {
                    //nothing to do, the definition already sits at its first remaining occurrence
                }
            }
        }
    }

    private record DoomedDefinition(ObjectNode node, String id, String className, Consumer<JsonNode> replaceWith) {}

    private enum PlacementResult {PLACED, ALREADY_PLACED, NOT_FOUND}

    private void collectDoomedDefinitions(ObjectNode dataNode, DataStorageMetadataDictionary dictionary, boolean insideDoomed, List<DoomedDefinition> out) {
        String className = new DataJsonNode(dataNode).getDataClassName();
        boolean doomed = insideDoomed || isRemovedClass(dictionary, className);
        for (Map.Entry<String, JsonNode> attribute : dataNode.properties()) {
            JsonNode attributeNode = attribute.getValue();
            boolean attributeDoomed = doomed || dictionary.isRemovedAttribute(className, attribute.getKey());
            if (attributeNode.isArray()) {
                ArrayNode arrayNode = (ArrayNode) attributeNode;
                for (int i = 0; i < arrayNode.size(); i++) {
                    final int index = i;
                    collectPossibleDefinition(arrayNode.get(i), value -> arrayNode.set(index, value), dictionary, attributeDoomed, out);
                }
            } else if (attributeNode.isObject()) {
                ObjectNode wrapper = (ObjectNode) attributeNode;
                collectPossibleDefinition(wrapper.get("v"), value -> wrapper.set("v", value), dictionary, attributeDoomed, out);
            }
        }
    }

    private void collectPossibleDefinition(JsonNode value, Consumer<JsonNode> replaceWith, DataStorageMetadataDictionary dictionary, boolean doomed, List<DoomedDefinition> out) {
        if (!isData(value)) {
            return;
        }
        ObjectNode definition = (ObjectNode) value;
        DataJsonNode definitionData = new DataJsonNode(definition);
        if (doomed && !isRemovedClass(dictionary, definitionData.getDataClassName())) {
            out.add(new DoomedDefinition(definition, definitionData.getId(), definitionData.getDataClassName(), replaceWith));
        }
        collectDoomedDefinitions(definition, dictionary, doomed, out);
    }

    /** document-order scan for the first remaining (non-doomed) occurrence of the definition's id: an id reference is
     * replaced with the definition, the definition node itself means it is already correctly placed */
    private PlacementResult placeAtFirstRemainingOccurrence(ObjectNode dataNode, DataStorageMetadataDictionary dictionary, DoomedDefinition doomed) {
        String className = new DataJsonNode(dataNode).getDataClassName();
        if (isRemovedClass(dictionary, className)) {
            return PlacementResult.NOT_FOUND;
        }
        for (Map.Entry<String, JsonNode> attribute : dataNode.properties()) {
            JsonNode attributeNode = attribute.getValue();
            if (dictionary.isRemovedAttribute(className, attribute.getKey())) {
                continue;
            }
            if (attributeNode.isArray()) {
                ArrayNode arrayNode = (ArrayNode) attributeNode;
                for (int i = 0; i < arrayNode.size(); i++) {
                    final int index = i;
                    PlacementResult result = placeAtOccurrence(arrayNode.get(i), value -> arrayNode.set(index, value), dictionary, doomed);
                    if (result != PlacementResult.NOT_FOUND) {
                        return result;
                    }
                }
            } else if (attributeNode.isObject()) {
                ObjectNode wrapper = (ObjectNode) attributeNode;
                PlacementResult result = placeAtOccurrence(wrapper.get("v"), value -> wrapper.set("v", value), dictionary, doomed);
                if (result != PlacementResult.NOT_FOUND) {
                    return result;
                }
            }
        }
        return PlacementResult.NOT_FOUND;
    }

    private PlacementResult placeAtOccurrence(JsonNode value, Consumer<JsonNode> replaceWith, DataStorageMetadataDictionary dictionary, DoomedDefinition doomed) {
        if (value == doomed.node) {
            return PlacementResult.ALREADY_PLACED;
        }
        if (value != null && value.isTextual() && value.asText().equals(doomed.id)) {
            replaceWith.accept(doomed.node);
            return PlacementResult.PLACED;
        }
        if (isData(value)) {
            return placeAtFirstRemainingOccurrence((ObjectNode) value, dictionary, doomed);
        }
        return PlacementResult.NOT_FOUND;
    }

    private boolean isRemovedClass(DataStorageMetadataDictionary dictionary, String className) {
        DataStorageMetadata dataStorageMetadata = dictionary.getDataStorageMetadata(className);
        return dataStorageMetadata != null && dataStorageMetadata.isRemovedClass();
    }

    /**
     * fix objects in removed attributes.
     *
     * References are serialized using JsonIdentityInfo
     * That means first occurrence is the object and following are just the ids
     * If the first occurrence is a removed attribute Jackson can't read the reference.
     *
     * @param idToChild idToChild
     */
    public void fixIdsDeepFromRoot(Map<String, DataJsonNode> idToChild){
        replaceIdRefsWidthFactoriesDeep(idToChild);

        //reset the ids, (first occurrence is the object following are id references)
        this.replaceDuplicateFactoriesWidthIdDeep(new HashSet<>());
    }

    private void visitAttributes(BiConsumer<JsonNode,Consumer<JsonNode>> attributeConsumer) {
        for (String attributeVariableName : this.getAttributes()) {
            JsonNode attributeValue = this.getAttributeValue(attributeVariableName);
            if (attributeValue != null) {
                if (attributeValue.isArray()) {

                    List<Integer> removed = new ArrayList<>();
                    for (int i=0;i<(attributeValue).size();i++) {
                        final int setIndex=i;
                        attributeConsumer.accept(attributeValue.get(i), (value) -> {
                            if (value==null) {
                                removed.add(setIndex);
                            } else {
                                ((ArrayNode) attributeValue).set(setIndex, value);
                            }
                        });
                        for (Integer removedIndex : removed) {
                            ((ArrayNode) attributeValue).remove(removedIndex);
                        }
                    }
                } else {
                    attributeConsumer.accept(attributeValue, (value) -> this.setAttributeValue(attributeVariableName, value));
                }

            }
        }
    }


    public ObjectNode getJsonNode(){
        return this.jsonNode;
    }

}
