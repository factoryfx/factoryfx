package de.factoryfx.javafx.javascript.editor.attribute.visualisation;

import com.google.common.base.Strings;
import com.google.javascript.jscomp.JSError;
import com.google.javascript.jscomp.SourceFile;
import de.factoryfx.javascript.data.attributes.types.Javascript;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ErrorsAndWarningsAssistant extends AssistantBase<Javascript<?>,List<JSError>> {

    private final List<SourceFile> externalSources;

    public ErrorsAndWarningsAssistant(List<SourceFile> externalSources, WeakReference<Consumer<List<JSError>>> consumer) {
        super(consumer);
        this.externalSources = JsUtils.copySourceFiles(externalSources);
    }

    @Override
    protected List<JSError> process(Javascript<?> input) {
        if (input == null || Strings.isNullOrEmpty(input.getCode()))
            return Collections.emptyList();
        return new ErrorsAndWarningsCompiler().createErrorsAndWarnings(externalSources,input);
    }

}
