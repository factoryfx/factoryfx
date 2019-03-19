package io.github.factoryfx.factory.log;

import com.fasterxml.jackson.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class)
public class FactoryLogEntryTreeItem {
    public final List<FactoryLogEntryTreeItem> children;
    public final FactoryLogEntry log;

    @JsonCreator
    public FactoryLogEntryTreeItem(@JsonProperty("log")FactoryLogEntry log, @JsonProperty("children")List<FactoryLogEntryTreeItem> children) {
        this.log = log;
        this.children = children;
    }

    private void collectToDeep(Set<FactoryLogEntry> items){
        if (items.add(this.log)){
            children.forEach(child -> child.collectToDeep(items));
        }
    }

    @JsonIgnore
    public Set<FactoryLogEntry> getListDeep(){
        final HashSet<FactoryLogEntry> items = new HashSet<>();
        collectToDeep(items);
        return items;
    }

}
