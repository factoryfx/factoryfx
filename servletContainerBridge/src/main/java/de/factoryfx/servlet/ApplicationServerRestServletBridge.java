package de.factoryfx.servlet;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import de.factoryfx.data.jackson.ObjectMapperBuilder;
import de.factoryfx.server.rest.MicroserviceResource;
import de.factoryfx.server.rest.server.AllExceptionMapper;
import de.factoryfx.server.rest.server.JettyServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;

import javax.servlet.ServletContext;
import java.util.Collections;
import java.util.List;

public class ApplicationServerRestServletBridge {
    private final MicroserviceResource resource;
    private UpdateableServlet updateableServlet;


    public ApplicationServerRestServletBridge(MicroserviceResource resource, UpdateableServlet updateableServlet) {
        this.resource = resource;
        this.updateableServlet=updateableServlet;
    }

    public UpdateableServlet getUpdateableServlet(){
        return updateableServlet;
    }

    private ResourceConfig createJerseyRestResource(List<Object> resource) {
        ResourceConfig resourceConfig = new ResourceConfig();
//        resourceConfig.register(resource);
        resource.forEach(resourceConfig::register);
        resourceConfig.register(new AllExceptionMapper());

        ObjectMapper mapper = ObjectMapperBuilder.buildNewObjectMapper();

        JacksonJaxbJsonProvider provider = new JacksonJaxbJsonProvider();
        provider.setMapper(mapper);
        resourceConfig.register(provider);

        org.glassfish.jersey.logging.LoggingFeature loggingFilter = new org.glassfish.jersey.logging.LoggingFeature(java.util.logging.Logger.getLogger(JettyServerFactory.class.getName()));
        resourceConfig.registerInstances(loggingFilter);
        return resourceConfig;
    }

    public void addInitialServlet(ServletContext servletContext) {
        if (updateableServlet==null){
            this.updateableServlet = new UpdateableServlet(new ServletContainer(createJerseyRestResource(Collections.singletonList(resource))));
            servletContext.addServlet("fdgd", this.updateableServlet).addMapping("/*");
        } else {
            updateableServlet.update(new ServletContainer(createJerseyRestResource(Collections.singletonList(resource))));
        }
    }
}

