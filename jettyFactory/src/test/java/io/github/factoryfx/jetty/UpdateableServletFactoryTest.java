package io.github.factoryfx.jetty;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UpdateableServletFactoryTest {

    @Test
    @SuppressWarnings("unchecked")
    public void test_path_validation(){
        UpdateableServletFactory updateableServletFactory = new UpdateableServletFactory();
        {
            ServletAndPathFactory servletAndPathFactory = new ServletAndPathFactory();
            servletAndPathFactory.pathSpec.set("/*");
            updateableServletFactory.servletAndPaths.add(servletAndPathFactory);
        }

        {
            ServletAndPathFactory servletAndPathFactory = new ServletAndPathFactory();
            servletAndPathFactory.pathSpec.set("/*");
            updateableServletFactory.servletAndPaths.add(servletAndPathFactory);
        }

        assertEquals(1,updateableServletFactory.internal().validateFlat().size());
    }

}