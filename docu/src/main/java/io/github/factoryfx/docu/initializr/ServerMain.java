package io.github.factoryfx.docu.initializr;

import java.lang.String;

/**
 * Application start */
public class ServerMain {
    public static void main(String[] args) {
        new ServerBuilder().builder().microservice().build().start();
    }
}
