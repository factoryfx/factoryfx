package de.factoryfx.factory.log;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.*;
import de.factoryfx.factory.FactoryBase;

@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class)
public class FactoryLogEntry {
    public final Class<? extends FactoryBase<?,?>> factoryClass;
    public final String displayText;
    public final List<FactoryLogEntry> children;
    public final List<FactoryLogEntryEvent> events;
    public final long id;

    @JsonCreator
    public FactoryLogEntry(@JsonProperty("factoryClass") Class<? extends FactoryBase<?, ?>> factoryClass, @JsonProperty("displayText") String displayText, @JsonProperty("children") List<FactoryLogEntry> children, @JsonProperty("events") List<FactoryLogEntryEvent> items, @JsonProperty("id")long id) {
        this.factoryClass = factoryClass;
        this.displayText = displayText;
        this.children = children;
        this.events = items;
        this.id = id;
    }

    @SuppressWarnings("unchecked")
    public FactoryLogEntry(FactoryBase<?,?> factoryBase) {
        this((Class<? extends FactoryBase<?, ?>>) factoryBase.getClass(), factoryBase.internal().hasCustomDisplayText()?factoryBase.internal().getDisplayText():"",new ArrayList<>(),new ArrayList<>(), ThreadLocalRandom.current().nextLong(Long.MAX_VALUE));
    }

    public boolean hasEvents(){
        for (FactoryLogEntry child: children){
            if (child.hasEvents()){
                return true;
            }
        }
        return !events.isEmpty();
    }

    @JsonIgnore
    public Set<FactoryLogEntry> getListDeep(){
        final HashSet<FactoryLogEntry> items = new HashSet<>();
        collectToDeep(items);
        return items;
    }

    private void collectToDeep(Set<FactoryLogEntry> items){
        if (items.add(this)){
            children.forEach(child -> child.collectToDeep(items));
        }
    }

    @JsonIgnore
    public String getFactoryDescription(){
        String result = factoryClass.getSimpleName();
        if (!displayText.isEmpty()) {
            result += "(" + displayText + ")";
        }
        return result;
    }
    private static final int PRINTED_COUNTER_LIMIT=500;
    private static class PrintedCounter{
        private int printedCounter;
        public void inc(){
            printedCounter++;
        }
        public boolean limitReached(){
            return printedCounter >= PRINTED_COUNTER_LIMIT;
        }
    }
    public void toString(StringBuilder stringBuilder, long deep, Set<FactoryLogEntry> printed, String prefix, boolean isTail, PrintedCounter printedCounter){
        if (printedCounter.limitReached()) {
            return;
        }
        if (deep>0){
            stringBuilder.append(prefix).append(isTail ? "└── " : "├── ");
        }
        if (!printed.add(this)){
            stringBuilder.append("@").append(this.id).append("\n");
            return;
        }
        printedCounter.inc();

        stringBuilder.append(getFactoryDescription());
        stringBuilder.append(": ");
        stringBuilder.append(events.stream().map(e -> (e.type + " " + e.durationNs + "ns")).collect(Collectors.joining(", ")));
        stringBuilder.append(", +").append(this.id);
        stringBuilder.append("\n");

        int counter=0;
        for (FactoryLogEntry child: children){
            child.toString(stringBuilder, deep+1, printed, prefix + (isTail ? "    " : "│   "), counter==children.size()-1,printedCounter);
            counter++;
        }

    }

    public String toStringFromRoot(){
        StringBuilder stringBuilder = new StringBuilder("\n");
        stringBuilder.append("Application Started:\n");
//        stringBuilder.append("total start duration: " + (totalDurationNs / 1000000.0) + "ms"+"\n");
        PrintedCounter printedCounter=new PrintedCounter();
        toString(stringBuilder,0,new HashSet<>(),"", true,printedCounter);
        if (printedCounter.limitReached()){
            stringBuilder.append("... (aborted log after "+PRINTED_COUNTER_LIMIT+" factories)");
        }

        return stringBuilder.toString();
    }



}
