package de.factoryfx.adminui.angularjs.factory.server.resourcehandler;

import org.jsoup.nodes.Document;

public interface FileContentProvider {
    boolean containsFile(String file);
    byte[] getFile(String file);

    final static String CUSTOMISABLE_CSS="customisableCss7636.css";
    public default void replaceIndexHtmlPlaceholder(Document doc){
        doc.select("#customisableCss").attr("href",CUSTOMISABLE_CSS);
    }
}
