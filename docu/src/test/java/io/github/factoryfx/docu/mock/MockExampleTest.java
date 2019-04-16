package io.github.factoryfx.docu.mock;

import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.builder.FactoryTreeBuilder;
import io.github.factoryfx.factory.jackson.SimpleObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.function.Consumer;
import java.util.function.Function;

public class MockExampleTest {

    @Test
    public void mock_example(){
        FactoryTreeBuilder<Printer, PrinterFactory,Void> builder = new FactoryTreeBuilder<>(PrinterFactory.class, ctx->{
            PrinterFactory factory = new PrinterFactory();
            factory.text.set("Hello World");
            return factory;
        });

        Printer printer = builder.microservice(printerFactory -> printerFactory.utility().mock(root -> {
            Printer mock = Mockito.mock(Printer.class);
            Mockito.when(mock.print()).thenReturn("mocked");
            return mock;
        })).withInMemoryStorage().build().start();

        Assertions.assertEquals("mocked",printer.print());
    }
}
