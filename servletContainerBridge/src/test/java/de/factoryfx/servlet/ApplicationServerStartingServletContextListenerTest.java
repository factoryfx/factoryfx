package de.factoryfx.servlet;

import de.factoryfx.data.storage.DataAndNewMetadata;
import de.factoryfx.server.rest.client.ApplicationServerRestClient;
import de.factoryfx.server.rest.client.RestClient;
import de.factoryfx.servlet.example.RootFactory;
import org.eclipse.jetty.server.NetworkTrafficServerConnector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.webapp.WebAppContext;

/**
 * Created by hbrackmann on 15.05.2017.
 */
public class ApplicationServerStartingServletContextListenerTest {

    public static void main(String[] args) throws Exception {
        final Server server = new Server();
        server.addConnector(new NetworkTrafficServerConnector(server));

        WebAppContext webapp = new WebAppContext("./src/test/resources/webapp", "/");
        webapp.getServletContext().setExtendedListenerTypes(true);
//        webapp.setContextPath("/");

        webapp.addEventListener(new FactoryfxServletContextListenerImpl());
        server.setHandler(webapp);

        new Thread(){
            @Override
            public void run() {
                try {
                    server.start();
                    server.join();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();

        Thread.sleep(3000);

        RestClient restClient = new RestClient("localhost", ((ServerConnector) server.getConnectors()[0]).getLocalPort(), "applicationServer", false, null, null);
        ApplicationServerRestClient<ServletContextAwareVisitor, RootFactory> applicationServerRestClient = new ApplicationServerRestClient<>(restClient, RootFactory.class, "", "");

        {
            DataAndNewMetadata<RootFactory> rootFactoryFactoryAndNewMetadata = applicationServerRestClient.prepareNewFactory();
            rootFactoryFactoryAndNewMetadata.root.stringAttribute.set("XXX111");
            System.out.print(rootFactoryFactoryAndNewMetadata.root.stringAttribute.get());
            applicationServerRestClient.updateCurrentFactory(rootFactoryFactoryAndNewMetadata, "comment1");
        }

        {
            DataAndNewMetadata<RootFactory> rootFactoryFactoryAndNewMetadata = applicationServerRestClient.prepareNewFactory();
            rootFactoryFactoryAndNewMetadata.root.stringAttribute.set("XXX222");
            System.out.print(rootFactoryFactoryAndNewMetadata.root.stringAttribute.get());
            applicationServerRestClient.updateCurrentFactory(rootFactoryFactoryAndNewMetadata, "comment2");
        }

        Thread.sleep(20000000);
    }

}