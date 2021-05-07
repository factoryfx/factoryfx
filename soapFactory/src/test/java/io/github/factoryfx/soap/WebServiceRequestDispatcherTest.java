package io.github.factoryfx.soap;

import io.github.factoryfx.soap.examplenoroot.HelloWorldNoXmlRootElement;
import io.github.factoryfx.soap.examplenoroot.OtherClass1;
import io.github.factoryfx.soap.examplenoroot.SoapDummyRequestNoRoot;
import io.github.factoryfx.soap.examplenoroot.SoapDummyRequestNoRoot2;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Set;

class WebServiceRequestDispatcherTest {

    @Test
    void test_collectWebParamAnnotatedParams() {
        Set<Class<?>> set = WebServiceRequestDispatcher.collectWebParamAnnotatedParams(HelloWorldNoXmlRootElement.class);

        Assertions.assertEquals(2, set.size());
        Assertions.assertTrue(set.contains(SoapDummyRequestNoRoot.class));
        Assertions.assertTrue(set.contains(SoapDummyRequestNoRoot2.class));
        Assertions.assertFalse(set.contains(OtherClass1.class));

    }

    @Test
    void test_collectWebParamAnnotatedParams_impl() {
        Set<Class<?>> set = WebServiceRequestDispatcher.collectWebParamAnnotatedParams(HelloWorldNoXmlRootElementImpl.class);

        Assertions.assertEquals(2, set.size());
        Assertions.assertTrue(set.contains(SoapDummyRequestNoRoot.class));
        Assertions.assertTrue(set.contains(SoapDummyRequestNoRoot2.class));
        Assertions.assertFalse(set.contains(OtherClass1.class));

    }


}