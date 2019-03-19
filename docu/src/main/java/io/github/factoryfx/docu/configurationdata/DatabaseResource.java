package io.github.factoryfx.docu.configurationdata;

import javax.ws.rs.GET;

public class DatabaseResource {
    private final String url;
    private final String user;
    private final String password;

    public DatabaseResource(String url, String user, String password) {
        this.url = url;
        this.user = user;
        this.password = password;
    }

    @GET
    public String get() {
        //connect with url,user and password and do something with database
        return "";
    }
}
