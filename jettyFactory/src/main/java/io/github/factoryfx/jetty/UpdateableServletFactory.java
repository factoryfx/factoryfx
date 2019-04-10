package io.github.factoryfx.jetty;

import io.github.factoryfx.factory.util.LanguageText;
import io.github.factoryfx.factory.validation.ValidationResult;
import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.attribute.dependency.FactoryListAttribute;

import java.util.HashSet;

public class UpdateableServletFactory<R extends FactoryBase<?,R>> extends FactoryBase<UpdateableServlet,R> {

    public final FactoryListAttribute<R,ServletAndPath,ServletAndPathFactory<R>> servletAndPaths = new FactoryListAttribute<R,ServletAndPath,ServletAndPathFactory<R>>().labelText("servletAndPaths");

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
