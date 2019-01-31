package de.factoryfx.jetty;

import de.factoryfx.data.util.LanguageText;
import de.factoryfx.data.validation.ValidationResult;
import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.atrribute.FactoryReferenceListAttribute;

import java.util.HashSet;

public class UpdateableServletFactory<V,R extends FactoryBase<?,V,R>> extends FactoryBase<UpdateableServlet,V,R> {

    @SuppressWarnings("unchecked")
    public final FactoryReferenceListAttribute<ServletAndPath,ServletAndPathFactory<V,R>> servletAndPaths = FactoryReferenceListAttribute.create(new FactoryReferenceListAttribute<>(ServletAndPathFactory.class).labelText("servletAndPaths"));

    public UpdateableServletFactory() {
        this.configLifeCycle().setCreator(() -> new UpdateableServlet(servletAndPaths.instances()));
        this.configLifeCycle().setUpdater(updateableServlet -> {
            updateableServlet.update(servletAndPaths.instances());
        });
        servletAndPaths.validation(list -> {
            HashSet<String> paths = new HashSet<>();
            for (ServletAndPathFactory<V, R> path : list) {
                if (!paths.add(path.pathSpec.get())){
                    return new ValidationResult(true,new LanguageText().en("duplicate path"));
                }
            }
            return new ValidationResult(false,new LanguageText());
        });
    }
}
