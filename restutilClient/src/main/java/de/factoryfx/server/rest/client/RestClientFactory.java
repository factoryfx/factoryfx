package de.factoryfx.server.rest.client;

import de.factoryfx.data.attribute.AttributeMetadata;
import de.factoryfx.data.attribute.types.BooleanAttribute;
import de.factoryfx.data.attribute.types.IntegerAttribute;
import de.factoryfx.data.attribute.types.StringAttribute;
import de.factoryfx.factory.SimpleFactoryBase;

public class RestClientFactory<V> extends SimpleFactoryBase<RestClient,V> {
    public final BooleanAttribute ssl=new BooleanAttribute(new AttributeMetadata().labelText("protocol"));
    public final StringAttribute host=new StringAttribute(new AttributeMetadata().labelText("host"));
    public final IntegerAttribute port=new IntegerAttribute(new AttributeMetadata().labelText("port"));
    public final StringAttribute path=new StringAttribute(new AttributeMetadata().labelText("path")).defaultValue("adminui");

    public final StringAttribute httpAuthenticationUser=new StringAttribute(new AttributeMetadata().labelText("httpAuthenticationUser"));
    public final StringAttribute httpAuthenticationPassword=new StringAttribute(new AttributeMetadata().labelText("httpAuthenticationPassword"));

    @Override
    public RestClient createImpl() {
        return new RestClient(host.get(),port.get(),path.get(),ssl.get(),httpAuthenticationUser.get(),httpAuthenticationPassword.get());
    }

}
