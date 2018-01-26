package de.factoryfx.server.rest.client;

import de.factoryfx.data.attribute.primitive.BooleanAttribute;
import de.factoryfx.data.attribute.primitive.IntegerAttribute;
import de.factoryfx.data.attribute.types.StringAttribute;
import de.factoryfx.factory.SimpleFactoryBase;

public class RestClientFactory<V> extends SimpleFactoryBase<RestClient,V> {
    public final BooleanAttribute ssl=new BooleanAttribute().labelText("ssl");
    public final StringAttribute host=new StringAttribute().labelText("host");
    public final IntegerAttribute port=new IntegerAttribute().labelText("port");
    public final StringAttribute path=new StringAttribute().labelText("path").defaultValue("adminui");

    public final StringAttribute httpAuthenticationUser=new StringAttribute().labelText("httpAuthenticationUser");
    public final StringAttribute httpAuthenticationPassword=new StringAttribute().labelText("httpAuthenticationPassword");

    @Override
    public RestClient createImpl() {
        return new RestClient(host.get(),port.get(),path.get(),ssl.get(),httpAuthenticationUser.get(),httpAuthenticationPassword.get());
    }

    public RestClientFactory(){
        config().setDisplayTextProvider(() -> (ssl.get() ? "https" : "http") + "://" + host.get() + ":" + port.get() + "/" + path.get() + "/");
    }

}
