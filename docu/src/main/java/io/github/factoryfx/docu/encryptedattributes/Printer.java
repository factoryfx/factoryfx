package io.github.factoryfx.docu.encryptedattributes;

public class Printer {
    private final String password;

    public Printer(String password) {
        this.password = password;
    }

    public void print() {
        System.out.println(password);
    }
}
