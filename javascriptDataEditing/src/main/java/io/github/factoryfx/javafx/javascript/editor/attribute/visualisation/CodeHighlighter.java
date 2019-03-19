package io.github.factoryfx.javafx.javascript.editor.attribute.visualisation;

import com.google.javascript.jscomp.parsing.parser.Scanner;
import com.google.javascript.jscomp.parsing.parser.Token;
import com.google.javascript.jscomp.parsing.parser.TokenType;
import com.google.javascript.jscomp.parsing.parser.util.ErrorReporter;
import com.google.javascript.jscomp.parsing.parser.util.SourcePosition;
import com.google.javascript.jscomp.parsing.parser.util.SourceRange;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CodeHighlighter {

    static final ErrorReporter NO_ERROR_REPORTER = new ErrorReporter() {
        @Override
        protected void reportError(SourcePosition location, String message) {
        }

        @Override
        protected void reportWarning(SourcePosition location, String message) {
        }
    };

    public List<Span> createSpans(String text) {
        if (text == null)
            return Collections.emptyList();
        ArrayList<Span> spans = new ArrayList<>();
        Scanner.CommentRecorder rec = (type, range, value) -> spans.add(new Span(range.start.offset,range.end.offset-range.start.offset, JsTokenType.COMMENT));
        Scanner scanner = new Scanner(NO_ERROR_REPORTER,rec, new com.google.javascript.jscomp.parsing.parser.SourceFile("intern",text));
        while (scanner.peekToken().type != TokenType.END_OF_FILE) {
            Token currentToken = scanner.nextToken();
            JsTokenType.byTokenType(currentToken.type).ifPresent(tt->{
                SourceRange range = currentToken.location;
                spans.add(new Span(range.start.offset, range.end.offset-range.start.offset, tt));
            });
        }
        return spans;
    }

}
