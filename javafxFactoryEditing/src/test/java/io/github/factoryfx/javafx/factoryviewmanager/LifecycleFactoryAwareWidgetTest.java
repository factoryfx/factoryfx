package io.github.factoryfx.javafx.factoryviewmanager;

import io.github.factoryfx.factory.testfactories.ExampleFactoryA;
import javafx.scene.Node;
import javafx.scene.layout.Region;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class LifecycleFactoryAwareWidgetTest {

    private static class CountingLifecycleWidget extends LifecycleFactoryAwareWidget<ExampleFactoryA> {
        int initContentCalls;
        int applyFactoryCalls;
        int refreshDataCalls;
        ExampleFactoryA lastAppliedFactory;

        @Override
        protected Node initContent() {
            initContentCalls++;
            return new Region();
        }

        @Override
        protected void applyFactory(ExampleFactoryA rootFactory) {
            applyFactoryCalls++;
            lastAppliedFactory = rootFactory;
        }

        @Override
        protected void refreshData() {
            refreshDataCalls++;
        }
    }

    @Test
    public void test_initContent_called_once_refreshData_called_per_open() {
        CountingLifecycleWidget widget = new CountingLifecycleWidget();

        Node content = widget.createContent();
        Assertions.assertEquals(1, widget.initContentCalls);
        Assertions.assertEquals(1, widget.refreshDataCalls);
        Assertions.assertSame(content, widget.getContent());

        Assertions.assertSame(content, widget.createContent(), "the same node must be returned on re-open");
        widget.createContent();
        Assertions.assertEquals(1, widget.initContentCalls, "static UI must be built only once");
        Assertions.assertEquals(3, widget.refreshDataCalls, "data must refresh on every open");
    }

    @Test
    public void test_first_factory_delivery_applies_without_extra_refresh() {
        CountingLifecycleWidget widget = new CountingLifecycleWidget();
        widget.createContent(); //open: refresh already started here, in parallel with the factory download

        ExampleFactoryA root = new ExampleFactoryA();
        widget.edit(root);

        Assertions.assertEquals(1, widget.applyFactoryCalls);
        Assertions.assertSame(root, widget.getCurrentFactory());
        Assertions.assertSame(root, widget.lastAppliedFactory);
        Assertions.assertEquals(1, widget.refreshDataCalls, "the open-time refresh must not be repeated on the first factory delivery");
    }

    @Test
    public void test_new_factory_version_applies_and_refreshes() {
        CountingLifecycleWidget widget = new CountingLifecycleWidget();
        widget.createContent();
        widget.edit(new ExampleFactoryA());

        ExampleFactoryA newRoot = new ExampleFactoryA();
        widget.edit(newRoot); //save/update

        Assertions.assertEquals(2, widget.applyFactoryCalls);
        Assertions.assertSame(newRoot, widget.getCurrentFactory());
        Assertions.assertEquals(2, widget.refreshDataCalls, "a new factory version must also refresh non-factory data");
    }

    @Test
    public void test_same_root_instance_is_deduplicated() {
        //robustness on framework versions that re-deliver the same root on every view open
        CountingLifecycleWidget widget = new CountingLifecycleWidget();
        widget.createContent();

        ExampleFactoryA root = new ExampleFactoryA();
        widget.edit(root);
        widget.edit(root);
        widget.edit(root);

        Assertions.assertEquals(1, widget.applyFactoryCalls);
        Assertions.assertEquals(1, widget.refreshDataCalls);
    }
}
