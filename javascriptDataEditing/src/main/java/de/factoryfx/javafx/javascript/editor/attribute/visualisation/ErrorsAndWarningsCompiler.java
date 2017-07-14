package de.factoryfx.javafx.javascript.editor.attribute.visualisation;

import com.google.common.base.Throwables;
import com.google.javascript.jscomp.*;
import com.google.javascript.jscomp.Compiler;
import de.factoryfx.javascript.data.attributes.types.Javascript;

import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class ErrorsAndWarningsCompiler {

    public List<JSError> createErrorsAndWarnings(List<SourceFile> externalSources, Javascript code) {
        try {
            Compiler compiler = new Compiler(new PrintStream(new DiscardOutputStream(),false,"UTF-8"));
            compiler.disableThreads();
            try {
                ArrayList<SourceFile> externals = new ArrayList<>();
                externals.addAll(externalSources);
                externals.add(SourceFile.fromCode("declarations",code.getHeaderCode()));
                externals.add(SourceFile.fromCode("apiDecl",code.getDeclarationCode()));
                ArrayList<SourceFile> internalSource = new ArrayList<>();
                internalSource.add(SourceFile.fromCode("intern", code.getCode()));
                Result res = compiler.compile(externals, internalSource, createCompilerOptions());
                ArrayList<JSError> warningsAndErrors = new ArrayList<>();
                Predicate<JSError> isFromIntern = e -> e.node == null || (!e.node.isFromExterns() && "intern".equals(e.node.getSourceFileName()));
//                Predicate<JSError> isFromIntern = e -> e.node == null || (!e.node.isFromExterns());
                Stream.of(res.errors).filter(isFromIntern).forEach(warningsAndErrors::add);
                Stream.of(res.warnings).filter(isFromIntern).forEach(warningsAndErrors::add);
                return warningsAndErrors;
            } catch (RuntimeException r) {
                try {
                    return Collections.singletonList(JSError.make(DiagnosticType.error("ERR_COMPILE_FAILURE", "Cannot compile due to exception: " + Throwables.getStackTraceAsString(r))));
                } catch (RuntimeException re) {
                    return Collections.singletonList(JSError.make(DiagnosticType.error("ERR_COMPILE_FAILURE", "Cannot compile due to exception: " + r.getMessage().replaceAll("[{}]", ""))));
                }
            }
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }

    }


    public static CompilerOptions createCompilerOptions() {
        CompilerOptions options = new CompilerOptions();
        CompilationLevel.SIMPLE_OPTIMIZATIONS.setOptionsForCompilationLevel(options);
        options.setNewTypeInference(true);
        options.setContinueAfterErrors(true);
        options.setCheckSymbols(true);
        options.setCheckSuspiciousCode(false);
        options.setInferTypes(true);
        options.setInferConst(true);
        options.setClosurePass(true);
        options.setPreserveDetailedSourceInfo(true);
        options.setSkipNonTranspilationPasses(false);
        options.setIncrementalChecks(CompilerOptions.IncrementalCheckMode.OFF);
        options.setCheckTypes(true);
        options.setChecksOnly(true);
        return options;
    }
}
