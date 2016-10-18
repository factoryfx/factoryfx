package de.factoryfx.server.angularjs.factory.server.resourcehandler;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.google.javascript.jscomp.CompilationLevel;
import com.google.javascript.jscomp.CompilerOptions;
import com.google.javascript.jscomp.JSError;
import com.google.javascript.jscomp.Result;
import com.google.javascript.jscomp.SourceFile;

class JavaScriptFile {
    public final byte[] content;
    public final byte[] contentMinified;

    JavaScriptFile(byte[] content, byte[] contentMinified, String path) {
        this.content = content;
        if (contentMinified==null){
            this.contentMinified=minify(content,path);
        } else {
            this.contentMinified = contentMinified;
        }
    }

    public byte[] minify(byte[] jsFileContent, String path) {
        List<SourceFile> externs = Collections.emptyList();
        List<SourceFile> inputs = Arrays.asList(SourceFile.fromCode(path, new String(jsFileContent, StandardCharsets.UTF_8)));

        CompilerOptions options = new CompilerOptions();
        options.setAngularPass(true);
        CompilationLevel.SIMPLE_OPTIMIZATIONS.setOptionsForCompilationLevel(options);


        com.google.javascript.jscomp.Compiler compiler = new com.google.javascript.jscomp.Compiler();
        Result result = compiler.compile(externs, inputs, options);

        if (result.success) {
            return compiler.toSource().getBytes(StandardCharsets.UTF_8);
        }

        JSError[] errors = result.errors;
        StringBuilder errorText = new StringBuilder();
        for (JSError error: errors){
            errorText.append(error.toString());
        }
        throw new IllegalArgumentException("Unable to minify, input source\n"+ errorText);
    }

    @Override
    public String toString() {
        return "JavaScriptFile as bytes (size: "+content.length+")";
    }
}
