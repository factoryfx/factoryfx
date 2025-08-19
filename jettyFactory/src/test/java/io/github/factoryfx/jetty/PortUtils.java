package io.github.factoryfx.jetty;

import java.io.IOException;
import java.net.ServerSocket;

public class PortUtils {

    public static int findAvailablePort() {
        try(ServerSocket socket = new ServerSocket(0)) {
            socket.getReuseAddress();
            return socket.getLocalPort();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
