package io.github.factoryfx.jetty;

import io.github.factoryfx.jetty.builder.JettyServerRootFactory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UpdateableServletFactoryTest {

    @Test
    public void test_path_validation(){
        UpdateableServletFactory<JettyServerRootFactory> updateableServletFactory = new UpdateableServletFactory<>();
        {
            ServletAndPathFactory<JettyServerRootFactory> servletAndPathFactory = new ServletAndPathFactory<>();
            servletAndPathFactory.pathSpec.set("/*");
            updateableServletFactory.servletAndPaths.add(servletAndPathFactory);
        }

        {
            ServletAndPathFactory<JettyServerRootFactory> servletAndPathFactory = new ServletAndPathFactory<>();
            servletAndPathFactory.pathSpec.set("/*");
            updateableServletFactory.servletAndPaths.add(servletAndPathFactory);
        }

        assertEquals(1,updateableServletFactory.internal().validateFlat().size());
    }

}