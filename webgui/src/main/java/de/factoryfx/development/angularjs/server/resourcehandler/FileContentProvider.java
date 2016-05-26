package de.factoryfx.development.angularjs.server.resourcehandler;

public interface FileContentProvider {
    boolean containsFile(String file);
    byte[] getFile(String file);
}
