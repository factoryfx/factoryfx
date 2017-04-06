package de.factoryfx.javafx.javascript.editor.attribute.visualisation;

import com.google.javascript.jscomp.SourceFile;
import de.factoryfx.javascript.data.attributes.types.Javascript;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.NavigableMap;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ContentAssistant extends AssistantBase<Javascript<?>,NavigableMap<Integer, List<Proposal>>> {

    private final List<SourceFile> externalSources;

    public ContentAssistant(List<SourceFile> externalSources, WeakReference<Consumer<NavigableMap<Integer, List<Proposal>>>> consumer) {
        super(consumer);
        this.externalSources = JsUtils.copySourceFiles(externalSources);
    }


    @Override
    protected NavigableMap<Integer, List<Proposal>> process(Javascript input) {
        ArrayList<SourceFile> sourceFileCopy = new ArrayList<>(externalSources);
        return new ContentAssist().findProposals(sourceFileCopy,input);
    }
}
