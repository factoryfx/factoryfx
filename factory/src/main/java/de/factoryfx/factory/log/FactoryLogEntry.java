package de.factoryfx.factory.log;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import com.fasterxml.jackson.annotation.*;
import de.factoryfx.factory.FactoryBase;

@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class)
public class FactoryLogEntry {
    public final Class<? extends FactoryBase<?,?>> factoryClass;
    public final String factoryDescription;
    public final long id;

    @JsonCreator
    public FactoryLogEntry(@JsonProperty("factoryClass") Class<? extends FactoryBase<?,?>> factoryClass, @JsonProperty("displayText") String factoryDescription, @JsonProperty("id")long id) {
        this.factoryClass = factoryClass;
        this.factoryDescription = factoryDescription;
        this.id = id;
    }

    @SuppressWarnings("unchecked")
    public FactoryLogEntry(FactoryBase<?,?> factoryBase) {
        this((Class<? extends FactoryBase<?,?>>) factoryBase.getClass(), factoryBase.internalFactory().getFactoryDisplayText(), ThreadLocalRandom.current().nextLong(Long.MAX_VALUE));
    }

    //field/method instead of list for performance

    @JsonProperty
    long createDurationNs;
    public void logCreate(long createDurationNs){
        this.createDurationNs=createDurationNs;
    }

    @JsonProperty
    long recreateDurationNs;
    public void logRecreate(long recreateDurationNs){
        this.recreateDurationNs=recreateDurationNs;
    }

    @JsonProperty
    long startDurationNs;
    public void logStart(long startDurationNs){
        this.startDurationNs=startDurationNs;
    }

    @JsonProperty
    long destroyDurationNs;
    public void logDestroy(long destroyDurationNs){
        this.destroyDurationNs=destroyDurationNs;
    }

    @JsonIgnore
    public List<FactoryLogEntryEvent> getEvents(){
        ArrayList<FactoryLogEntryEvent> events = new ArrayList<>();
        if (createDurationNs!=0) {
            events.add(new FactoryLogEntryEvent(FactoryLogEntryEventType.CREATE,createDurationNs));
        }
        if (recreateDurationNs!=0) {
            events.add(new FactoryLogEntryEvent(FactoryLogEntryEventType.RECREATE,recreateDurationNs));
        }
        if (startDurationNs!=0) {
            events.add(new FactoryLogEntryEvent(FactoryLogEntryEventType.START,startDurationNs));
        }
        if (destroyDurationNs!=0) {
            events.add(new FactoryLogEntryEvent(FactoryLogEntryEventType.DESTROY,destroyDurationNs));
        }
        return events;
    }


    public String getFactoryDescription() {
        return factoryDescription;
    }
}
