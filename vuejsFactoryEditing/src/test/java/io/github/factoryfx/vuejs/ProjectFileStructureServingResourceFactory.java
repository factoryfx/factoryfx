package io.github.factoryfx.vuejs;

import de.factoryfx.factory.SimpleFactoryBase;

public class ProjectFileStructureServingResourceFactory extends SimpleFactoryBase<ProjectFileStructureServingResource,Void> {

    @Override
    public ProjectFileStructureServingResource createImpl() {
        return new ProjectFileStructureServingResource();
    }
}
