package de.factoryfx.jetty.ssl;

/**https://docs.oracle.com/javase/8/docs/technotes/guides/security/StandardNames.html#KeyStore*/
public enum KeyStoreType {
    jceks("jceks"),
    jks ("jks"),
    dks ("dks"),
    pkcs11("pkcs11"),
    pkcs12("pkcs12");

    private String value;
    KeyStoreType(String value){
        this.value=value;
    }

    public String value(){
        return value;
    }

}
