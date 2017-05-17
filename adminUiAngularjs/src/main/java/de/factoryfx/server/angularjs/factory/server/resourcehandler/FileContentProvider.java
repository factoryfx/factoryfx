package de.factoryfx.server.angularjs.factory.server.resourcehandler;

import org.jsoup.nodes.Document;

public interface FileContentProvider {
    boolean containsFile(String file);
    byte[] getFile(String file);

    String CUSTOMISABLE_CSS="customisableCss7636.css";
    default void replaceIndexHtmlPlaceholder(Document doc){
        doc.select("#customisableCss").attr("href",CUSTOMISABLE_CSS);
    }
}
