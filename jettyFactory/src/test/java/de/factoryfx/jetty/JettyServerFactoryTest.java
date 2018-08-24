package de.factoryfx.jetty;

import de.factoryfx.data.jackson.ObjectMapperBuilder;
import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.SimpleFactoryBase;
import de.factoryfx.factory.atrribute.FactoryReferenceAttribute;
import de.factoryfx.jetty.JettyServerFactory;
import org.junit.Test;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import java.util.Arrays;
import java.util.List;

public class JettyServerFactoryTest {
    @Path("/Resource1")
    public static class Resource1{
        @GET()
        public Response get(){
            return Response.ok().build();
        }
    }

    public static class TestWebserverFactory extends JettyServerFactory<Void,TestWebserverFactory> {
        public final FactoryReferenceAttribute<Resource1,Resource1Factory> resource = new FactoryReferenceAttribute<>(Resource1Factory.class);

        @Override
        protected List<Object> getResourcesInstances() {
            return Arrays.asList(resource.instance());
        }
    }

    public static class Resource1Factory extends SimpleFactoryBase<Resource1,Void,TestWebserverFactory> {
        @Override
        public Resource1 createImpl() {
            return new Resource1();
        }
    }

    @Test
    public void test_json(){
        TestWebserverFactory factoryBases = new TestWebserverFactory();
        factoryBases.resource.set(new Resource1Factory());

        ObjectMapperBuilder.build().copy(factoryBases);
    }

}