package de.factoryfx.javafx.javascript.editor.attribute.visualisation;

import de.factoryfx.javascript.data.attributes.types.Javascript;

import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public class CodeHighlightingAssistant extends AssistantBase<Javascript<?>,List<Span>> {

    public CodeHighlightingAssistant(WeakReference<Consumer<List<Span>>> consumer) {
        super(consumer);
    }

    @Override
    protected List<Span> process(Javascript<?> input) {
        return input != null?new CodeHighlighter().createSpans(input.getCode()): Collections.emptyList();
    }

}
