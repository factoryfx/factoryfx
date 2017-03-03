package de.factoryfx.factory.log;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import de.factoryfx.factory.FactoryBase;

@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class FactoryLogEntry {
    public final String id;
    public final String displayText;
    public final List<FactoryLogEntry> children;
    public final List<FactoryLogEntryEvent> events;

    @JsonCreator
    public FactoryLogEntry(@JsonProperty("id")String id,@JsonProperty("displayText")String displayText, @JsonProperty("children")List<FactoryLogEntry> children, @JsonProperty("events")List<FactoryLogEntryEvent> items) {
        this.displayText = displayText;
        this.children = children;
        this.events = items;
        this.id = id;
    }


    public FactoryLogEntry(FactoryBase<?,?> factoryBase) {
        this(factoryBase.getId(),factoryBase.internal().getDisplayText(),new ArrayList<>(),new ArrayList<>());
    }
}
