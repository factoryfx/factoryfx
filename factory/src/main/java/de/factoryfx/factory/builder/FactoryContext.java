package de.factoryfx.factory.builder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Stream;

import de.factoryfx.data.Data;
import de.factoryfx.factory.FactoryBase;

public class FactoryContext<R extends FactoryBase<?,?,R>> {

    private final List<FactoryCreator<?,R>> factoryCreators = new ArrayList<>();

    @SuppressWarnings("unchecked")
    public <L, F extends FactoryBase<?,?,R>> F get(Predicate<FactoryCreator<?,R>> filter){
        return factoryCreators.stream().filter(filter).findAny().map(rFactoryCreator -> (F) rFactoryCreator.create(this)).orElse(null);
    }

    public <F extends FactoryBase<?,?,R>> F get(Class<F> clazz){
        F result = get(fc -> fc.match(clazz));
        if (result==null){
           throw new IllegalStateException("builder missing Factory: "+clazz);
        }
        return result;
    }

    public <F extends FactoryBase<?,?,R>> F get(Class<F> clazz, String name){
        F result = get(fc -> fc.match(clazz) && fc.match(name));
        if (result==null){
            throw new IllegalStateException("builder missing Factory: "+clazz + "and name: "+name);
        }
        return result;
    }

    void addFactoryCreator(FactoryCreator<?,R> factoryCreator){
        factoryCreators.add(factoryCreator);
    }


    @SuppressWarnings("unchecked")
    public <L, F extends FactoryBase<L,?,R>> List<F> getList(Class<F> clazz) {
        ArrayList<F> result = new ArrayList<>();
        factoryCreators.stream().filter(fc -> fc.match(clazz)).forEach(vFactoryCreator -> result.add((F) vFactoryCreator.create(FactoryContext.this)));
        return result;
    }

    public <L, F extends FactoryBase<L,?,R>> boolean anyMatch(Class<F> clazz){
        return factoryCreators.stream().anyMatch(fc -> fc.match(clazz));
    }

    public Scope getScope(Class<?> factoryClazz) {
        return factoryCreators.stream().filter(fc -> fc.match(factoryClazz)).findAny().map(FactoryCreator::getScope).orElse(null);
    }

    boolean isEmpty() {
        return factoryCreators.stream().allMatch(FactoryCreator::isEmpty);
    }

    @SuppressWarnings("unchecked")
    public void fillFromExistingFactoryTree(R root) {
        List<FactoryBase<?,?,?>> factories = root.internalFactory().collectChildFactoriesDeepFromRoot();
        Map<FactoryCreatorIdentifier,FactoryBase<?,?,?>> classToFactory = new HashMap<>();
        for (FactoryBase<?,?,?> factory : factories) {
            classToFactory.put(new FactoryCreatorIdentifier(factory.getClass(),factory.internalFactory().getTreeBuilderName()),factory);
        }

        for (FactoryCreator<?, R> factoryCreator : factoryCreators) {
            factoryCreator.fillFromExistingFactoryTree(classToFactory);
        }

        //.stream().allMatch(FactoryCreator::isEmpty);
    }
}
