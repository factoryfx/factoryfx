package de.factoryfx.servlet.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import de.factoryfx.data.jackson.ObjectMapperBuilder;
import de.factoryfx.server.rest.ApplicationServerResource;
import de.factoryfx.server.rest.server.AllExceptionMapper;
import de.factoryfx.server.rest.server.JettyServerFactory;
import de.factoryfx.servlet.ApplicationServerRestServletBridge;
import de.factoryfx.servlet.UpdateableServlet;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;

import javax.servlet.ServletContext;
import java.util.Arrays;
import java.util.List;

/**
 * Created by hbrackmann on 15.05.2017.
 */
public class Root {

    public Root(ApplicationServerRestServletBridge instance) {

    }
}
