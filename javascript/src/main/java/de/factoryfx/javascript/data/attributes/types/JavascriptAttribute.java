package de.factoryfx.javascript.data.attributes.types;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.javascript.jscomp.SourceFile;
import de.factoryfx.data.Data;
import de.factoryfx.data.attribute.AttributeChangeListener;
import de.factoryfx.data.attribute.ImmutableValueAttribute;
import javafx.application.Platform;

public class JavascriptAttribute<A> extends ImmutableValueAttribute<Javascript<A>,JavascriptAttribute<A>> {

    @JsonIgnore
    private final Supplier<List<? extends Data>> data;
    @JsonIgnore
    private final Class<A> apiClass;
    @JsonIgnore
    private final Function<List<? extends Data>,String> headerCreator = this::defaultCreateHeader;

    @SuppressWarnings("unchecked")
    public JavascriptAttribute(Supplier<List<? extends Data>> data, Class<A> apiClass) {
        super((Class<Javascript<A>>)Javascript.class.asSubclass(Javascript.class));
        this.data = data;
        this.apiClass = apiClass;
        set(new Javascript<>("",createHeader(),createHeaderApi()));
    }

    @Override
    public boolean internal_mergeMatch(Javascript<A> value) {
        if (this.value == null && value == null)
            return true;
        if (this.value== null)
            return false;
        return Objects.equals(this.value.getCode(), value.getCode());
    }

    @Override
    public void internal_copyTo(JavascriptAttribute<A> copyAttribute, Function<Data, Data> dataCopyProvider) {
        if (copyAttribute.get()==null){
            copyAttribute.set(new Javascript<>());
        } else {
            copyAttribute.set(new Javascript<>(copyAttribute.get().getCode()));
        }
    }

    public List<SourceFile> internal_getExterns() {
        return Externs.get();
    }

    private String createHeaderApi(){
        DeclareJavaInput createDecl = new DeclareJavaInput();
        createDecl.declareVariable("api",apiClass);
        return createDecl.sourceScript();
    }


    private String createHeader() {
        return headerCreator.apply(data.get());
    }

    private String defaultCreateHeader(List<? extends Data> list) {
        StringBuilder sb = new StringBuilder();
        if (list.size() == 1) {
            sb.append("var data = ");
        } else {
            sb.append("var data = [");
        }
        int initialLen = sb.length();
        for (Data d : list) {
            if (d != null) {
                writeData(sb, d);
                sb.append(',');
            } else {
                sb.append("null,");
            }
        }
        if (sb.length() > initialLen) {
            sb.setLength(sb.length()-1);
        }
        if (list.size() == 1) {
            sb.append(";\n");
        } else {
            sb.append("];\n");
        }
        return sb.toString();
    }

    private void writeData(StringBuilder sb, Data d) {
        sb.append("{");
        ObjectMapper mapper = new ObjectMapper();
        int oldLen = sb.length();
        d.internal().visitAttributesFlat((name, attribute) -> {
            try {
                Object value = attribute.get();

                sb.append("\"").append(name).append("\" : ");
                if (value instanceof Data) {
                    writeData(sb,(Data)value);
                } else {
                    sb.append(mapper.writeValueAsString(value));
                }
                sb.append(",");

            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        });
        if (sb.length() > oldLen) {
            sb.setLength(sb.length() - 1);
        }
        sb.append("}");
    }


    @Override
    public Javascript<A> get() {
        Javascript<A> currentValue = new Javascript<>(super.get().getCode(),createHeader(),createHeaderApi());
        if (!currentValue.match(super.get())){
            set(currentValue);
        }
        return super.get();
    }

    //** so we don't need to initialise javax toolkit in test*/
    private Consumer<Runnable> runlaterExecutor= Platform::runLater;
    void setRunlaterExecutorForTest(Consumer<Runnable> runlaterExecutor){
        this.runlaterExecutor=runlaterExecutor;
    }

    void runLater(Runnable runnable){
        runlaterExecutor.accept(runnable);
    }

    class DirtyTrackingThread extends Thread{
        volatile boolean tracking=true;
        Javascript<A> previousValue=get();
        @Override
        public void run() {
            super.run();
            while(tracking){
                Javascript<A> currentValue = get();
                if (!currentValue.getHeaderCode().equals(previousValue.getHeaderCode())){
                    set(currentValue);
                }
                previousValue=currentValue;
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

    private DirtyTrackingThread dirtyTracking;

    @Override
    public void internal_addListener(AttributeChangeListener<Javascript<A>,JavascriptAttribute<A>> listener) {
        super.internal_addListener(listener);
        if (dirtyTracking==null){
            dirtyTracking = new DirtyTrackingThread();
            dirtyTracking.setDaemon(true);
            dirtyTracking.start();
        }
    }
    @Override
    public void internal_removeListener(AttributeChangeListener<Javascript<A>,JavascriptAttribute<A>> listener) {
        super.internal_removeListener(listener);
        if (listenersEmpty() && dirtyTracking != null){
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


}
