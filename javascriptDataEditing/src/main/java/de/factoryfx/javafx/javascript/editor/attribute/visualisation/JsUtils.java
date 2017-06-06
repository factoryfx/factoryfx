package de.factoryfx.javafx.javascript.editor.attribute.visualisation;

import com.google.javascript.jscomp.SourceFile;
import com.google.javascript.jscomp.parsing.parser.util.ErrorReporter;
import com.google.javascript.jscomp.parsing.parser.util.SourcePosition;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

public class JsUtils {

    public static final ErrorReporter NULL_ERROR_REPORTER = new ErrorReporter() {
        @Override
        protected void reportError(SourcePosition location, String message) {
        }

        @Override
        protected void reportWarning(SourcePosition location, String message) {
        }
    };

    public static ArrayList<SourceFile> copySourceFiles(Collection<SourceFile> externalSources) {
        ArrayList<SourceFile> sourceFileCopy = new ArrayList<>();
        externalSources.forEach(sf->{
            try {
                SourceFile sourceFile = SourceFile.fromCode(sf.getName(),sf.getCode());
                sourceFileCopy.add(sourceFile);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        return sourceFileCopy;
    }
}
