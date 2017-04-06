package de.factoryfx.javascript.data.attributes.types;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.javascript.jscomp.SourceFile;
import de.factoryfx.data.Data;
import de.factoryfx.data.attribute.*;
import javafx.application.Platform;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class JavascriptAttribute<A> extends ValueAttribute<Javascript<A>> {

    @JsonIgnore
    public final Supplier<List<Data>> data;
    @JsonIgnore
    public final Class<A> apiClass;
    @JsonIgnore
    private final Function<List<Data>,String> headerCreator = l->defaultCreateHeader(l);


    public JavascriptAttribute(AttributeMetadata attributeMetadata, Supplier<List<Data>> data, Class<A> apiClass) {
        super(attributeMetadata, (Class<Javascript<A>>)Javascript.class.asSubclass(Javascript.class));
        this.data = data;
        this.apiClass = apiClass;
        set(new Javascript<A>());
    }

    @JsonCreator
    protected JavascriptAttribute(Javascript<A> initialValue) {
        super(null, (Class<Javascript<A>>)Javascript.class.asSubclass(Javascript.class));
        data = null;
        this.apiClass = null;
        set(initialValue);
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public Javascript<A> get() {
        return super.get();
    }

    @Override
    public boolean internal_match(Javascript<A> value) {
        if ((this.value == null) != (value == null))
            return false;
        return Objects.equals(this.value.getCode(), value.getCode());
    }

    @Override
    public void internal_copyTo(Attribute<Javascript<A>> copyAttribute, Function<Data, Data> dataCopyProvider) {
        copyAttribute.set(get().copy());
    }

    public List<SourceFile> getExterns() {
        return Externs.get();
    }

    public Supplier<String> getDeclarationsSupplier() {
        return ()->data2declaration();
    }

    private String data2declaration() {
        return "";
    }



    //** so we don't need to initialise javax toolkit in test*/
    Consumer<Runnable> runlaterExecutor=(r)-> Platform.runLater(r);
    void setRunlaterExecutorForTest(Consumer<Runnable> runlaterExecutor){
        this.runlaterExecutor=runlaterExecutor;
    }

    public void runLater(Runnable runnable){
        runlaterExecutor.accept(runnable);
    }

    class DirtyTrackingThread extends Thread{
        volatile boolean tracking=true;
        @Override
        public void run() {
            super.run();
            while(tracking){
                Javascript<A> currentValue = get();
                String newHeader = createHeader();
                String oldHeader = currentValue.getHeaderCode();
                if (!Objects.equals(newHeader,oldHeader)) {
                    runLater(()->set(currentValue.copyWithNewHeaderCode(newHeader)));
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        public void stopTracking() {
            tracking=false;
        }
    }

    DirtyTrackingThread dirtyTracking;

    @Override
    public void internal_addListener(AttributeChangeListener<Javascript<A>> listener) {
        super.internal_addListener(listener);
        if (dirtyTracking==null){
            dirtyTracking = new DirtyTrackingThread();
            dirtyTracking.setDaemon(true);
            dirtyTracking.start();
        }
    }
    @Override
    public void internal_removeListener(AttributeChangeListener<Javascript<A>> listener) {
        super.internal_removeListener(listener);
        if (listeners.isEmpty() && dirtyTracking != null){
            dirtyTracking.stopTracking();
            dirtyTracking=null;
        }
    }

    @Override
    public void internal_endUsage() {
        if (dirtyTracking!=null) {
            dirtyTracking.stopTracking();
        }
        super.internal_endUsage();
    }

    private String createHeader() {
        return headerCreator.apply(data.get());
    }

    protected String defaultCreateHeader(List<Data> l) {
        StringBuilder sb = new StringBuilder();
        if (l.size() == 1) {
            sb.append("var data = ");
        } else {
            sb.append("var data = [");
        }
        int initialLen = sb.length();
        for (Data d : l) {
            if (d != null) {
                sb.append("{");
                ObjectMapper mapper = new ObjectMapper();
                int oldLen = sb.length();
                d.internal().visitAttributesFlat((name, attribute) -> {
                    try {
                        sb.append("\"" + name + "\" : ");
                        sb.append(mapper.writeValueAsString(attribute.get()));
                        sb.append(",");
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                });
                if (sb.length() > oldLen) {
                    sb.setLength(sb.length() - 1);
                }
                sb.append("},");
            } else {
                sb.append("null,");
            };
        }
        if (sb.length() > initialLen) {
            sb.setLength(sb.length()-1);
        }
        if (l.size() == 1) {
            sb.append(";\n");
        } else {
            sb.append("];\n");
        }
        return sb.toString();
    }



}
