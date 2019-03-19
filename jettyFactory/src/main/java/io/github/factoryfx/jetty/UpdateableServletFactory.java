package io.github.factoryfx.jetty;

import io.github.factoryfx.data.util.LanguageText;
import io.github.factoryfx.data.validation.ValidationResult;
import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.atrribute.FactoryReferenceListAttribute;

import java.util.HashSet;

public class UpdateableServletFactory<R extends FactoryBase<?,R>> extends FactoryBase<UpdateableServlet,R> {

    @SuppressWarnings("unchecked")
    public final FactoryReferenceListAttribute<ServletAndPath,ServletAndPathFactory<R>> servletAndPaths = FactoryReferenceListAttribute.create(new FactoryReferenceListAttribute<>(ServletAndPathFactory.class).labelText("servletAndPaths"));

    public UpdateableServletFactory() {
        this.configLifeCycle().setCreator(() -> new UpdateableServlet(servletAndPaths.instances()));
        this.configLifeCycle().setUpdater(updateableServlet -> {
            updateableServlet.update(servletAndPaths.instances());
        });
        servletAndPaths.validation(list -> {
            HashSet<String> paths = new HashSet<>();
            for (ServletAndPathFactory<R> path : list) {
                if (!paths.add(path.pathSpec.get())){
                    return new ValidationResult(true,new LanguageText().en("duplicate path"));
                }
            }
            return new ValidationResult(false,new LanguageText());
        });
    }
}
