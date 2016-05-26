package de.factoryfx.model.merge;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.google.common.base.Strings;
import de.factoryfx.model.FactoryBase;
import de.factoryfx.model.attribute.Attribute;

public class MergeResultEntry<T extends FactoryBase<?,T>> {
    private final T previousData;
    private final Optional<T> newData;
    private final Optional<Attribute<?>> previousDataAttribute;
    private final Optional<Attribute<?>> newDataAttribute;
    private List<FactoryBase<?,?>> path;

    public MergeResultEntry(T previousData, Optional<T> newData, Optional<Attribute<?>> previousDataAttribute, Optional<Attribute<?>> newDataAttribute) {
        this.previousData = previousData;
        this.newData = newData;
        this.previousDataAttribute = previousDataAttribute;
        this.newDataAttribute = newDataAttribute;
    }

    public String getNewEntity() {
        if (newData.isPresent()) {
            return newData.get().getDescriptiveName();
        }
        return "";
    }

    //*changed entity*/
    public FactoryBase<?,?> getNewEntityModel() {
        return newData.get();
    }

    public String getNewField() {
        if (newDataAttribute.isPresent()) {
            return newDataAttribute.get().metadata.displayName;
        }
        return "empty";
    }

    public String getNewValue() {
        if (newDataAttribute.isPresent()) {
            return getValueString(newDataAttribute.get().get());
        }
        return "empty";
    }

    public String getPathDisplayText() {
        return path.stream().map(pathElement -> pathElement.getDescriptiveName()).collect(Collectors.joining("/"));
    }

    public String getPreviousEntity() {
        return previousData.getDescriptiveName();
    }

    public FactoryBase<?,?> getPreviousEntityModel() {
        return previousData;
    }

    public String getPreviousField() {
        if (previousDataAttribute.isPresent()) {
            return previousDataAttribute.get().metadata.displayName;
        }
        return "empty";
    }

    public String getPreviousValue() {
        if (previousDataAttribute.isPresent()) {
            return getValueString(previousDataAttribute.get().get());
        }
        return "empty";
    }

    private String getValueString(Object object) {
        if (object instanceof Collection<?>) {
            Collection<?> list = (Collection<?>) object;
            StringBuilder stringBuilder = new StringBuilder("List (number of entries: " + list.size() + ")\n");
            for (Object item : list) {
                stringBuilder.append(getValueString(item));
                stringBuilder.append(",\n");
            }
            return stringBuilder.toString();
        }
        if (object instanceof Map<?, ?>) {
            Map<?, ?> map = (Map<?, ?>) object;
            StringBuilder stringBuilder = new StringBuilder("Map (number of entries: " + map.size() + ")\n");
            for (Map.Entry<?, ?> item : map.entrySet()) {
                stringBuilder.append(getValueString(item.getKey()));
                stringBuilder.append(": ");
                stringBuilder.append(getValueString(item.getValue()));
                stringBuilder.append(",\n");
            }
            return stringBuilder.toString();
        }
        if (object instanceof FactoryBase) {
            Object value = ((FactoryBase) object).getDescriptiveName();
            if (value == null || Strings.isNullOrEmpty(value.toString())) {
                return "empty";
            }
            return (String) value;
        }
        if (object == null) {
            return "empty";
        }
        return object.toString();
    }

    public void setPath(List<FactoryBase<?,?>> path) {
        this.path = path;
    }
}
