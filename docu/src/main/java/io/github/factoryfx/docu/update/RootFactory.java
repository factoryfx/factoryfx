package io.github.factoryfx.docu.update;

import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.attribute.types.StringAttribute;

public class RootFactory extends FactoryBase<Root, RootFactory> {
    public final StringAttribute stringAttribute = new StringAttribute();

    public RootFactory() {
        configLifeCycle().setCreator(() -> new Root(stringAttribute.get()));
        configLifeCycle().setStarter(root -> {
            //artificial long start time
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        configLifeCycle().setUpdater(root -> root.setDummy(stringAttribute.get()));

        // You can also conditionally recreate the expensive resource
        //configLifeCycle().setReCreator(root -> {
        //    if (stringAttribute.get().equals("someCondition")) {
        //        return new Root(stringAttribute.get());
        //    } else {
        //        root.setDummy(stringAttribute.get());
        //        return root;
        //    }
        //});
    }
}
