package de.factoryfx.adminui.angularjs.server.resourcehandler;

public interface FileContentProvider {
    boolean containsFile(String file);
    byte[] getFile(String file);
}
