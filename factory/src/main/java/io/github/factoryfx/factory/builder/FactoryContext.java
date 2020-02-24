package io.github.factoryfx.factory.builder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import io.github.factoryfx.factory.FactoryBase;

public class FactoryContext<R extends FactoryBase<?,R>> {

    private final List<FactoryCreator<?,R>> factoryCreators = new ArrayList<>();

    @SuppressWarnings("unchecked")
    public <F extends FactoryBase<?,R>> F get(Predicate<FactoryCreator<?,R>> filter){
        return factoryCreators.stream().filter(filter).findAny().map(rFactoryCreator -> (F) rFactoryCreator.create(this)).orElse(null);
    }

    <F extends FactoryBase<?,R>> F getUnchecked(Class<?> clazz){
        return get(fc -> fc.match(clazz,null));
    }

    public <F extends FactoryBase<?,R>> F get(String name){
        F result = get(fc -> fc.match(null,name));
        if (result==null){
            throw new IllegalStateException("builder missing, name: "+name);
        }
        return result;
    }

    public <F extends FactoryBase<?,R>> F get(Class<F> clazz){
        F result = get(fc -> fc.match(clazz,null));
        if (result==null){
            throw new IllegalStateException("builder missing, factory: "+clazz);
        }
        return result;
    }


    public <F extends FactoryBase<?,R>> F getUnsafe(Class<?> clazz){
        F result = get(fc -> fc.match(clazz,null));
        if (result==null){
            throw new IllegalStateException("builder missing, factory: "+clazz);
        }
        return result;
    }

    public <F extends FactoryBase<?,R>> F get(Class<F> clazz, String name){
        F result = get(fc -> fc.match(clazz,name));
        if (result==null){
            throw new IllegalStateException("builder missing, factory: "+clazz+ " name: "+name);
        }
        return result;
    }

    public <F extends FactoryBase<?,R>> F getUnsafe(Class<?> clazz, String name){
        F result = get(fc -> fc.match(clazz,name));
        if (result==null){
            throw new IllegalStateException("builder missing, factory: "+clazz+ " name: "+name);
        }
        return result;
    }

    public <F extends FactoryBase<?,R>> F get(FactoryTemplateId<F> factoryTemplateId){
        F result = get(fc -> fc.match(factoryTemplateId.clazz,factoryTemplateId.name));
        if (result==null){
            throw new IllegalStateException("builder missing, factory: "+factoryTemplateId.clazz+ " name: "+factoryTemplateId.name);
        }
        return result;
    }

    void addFactoryCreator(FactoryCreator<?,R> factoryCreator){
        for (FactoryCreator<?, R> creator : factoryCreators) {
            if (factoryCreator.isDuplicate(creator)){
                throw new IllegalArgumentException("duplicate factory registration: "+factoryCreator);
            }
        }
        factoryCreators.add(factoryCreator);
    }


    @SuppressWarnings("unchecked")
    <L, F extends FactoryBase<L,R>> List<F> getList(Class<F> clazz) {
        ArrayList<F> result = new ArrayList<>();
        factoryCreators.stream().filter(fc -> fc.match(clazz)).forEach(vFactoryCreator -> result.add((F) vFactoryCreator.create(FactoryContext.this)));
        return result;
    }

    @SuppressWarnings("unchecked")
    <L, F extends FactoryBase<L,R>> List<F> getListFromLiveObjectClass(Class<L> liveObjectCLass, Class<?> fromClass) {
        ArrayList<F> result = new ArrayList<>();
        for (FactoryCreator<?, R> fc : factoryCreators) {
            if (fc.matchLiveObjectClass(liveObjectCLass) && (fromClass==null || !fc.match(fromClass))) {
                result.add((F) fc.create(FactoryContext.this));
            }
        }
        return result;
    }

    public <L, F extends FactoryBase<L,R>> boolean anyMatch(Class<F> clazz){
        return factoryCreators.stream().anyMatch(fc -> fc.match(clazz));
    }

    public Scope getScope(FactoryTemplateId<?> factoryTemplateId) {
        return factoryCreators.stream().filter(fc -> fc.match(factoryTemplateId.clazz,factoryTemplateId.name)).findAny().map(FactoryCreator::getScope).orElse(null);
    }

    boolean isEmpty() {
        return factoryCreators.stream().allMatch(FactoryCreator::isEmpty);
    }

    public void fillFromExistingFactoryTree(R root) {
        List<FactoryBase<?,R>> factories = root.internal().collectChildrenDeep();
        Map<FactoryCreatorIdentifier,FactoryBase<?,?>> classToFactory = new HashMap<>();
        for (FactoryBase<?,?> factory : factories) {
            classToFactory.put(new FactoryCreatorIdentifier(factory.getClass(),factory.internal().getTreeBuilderName()),factory);
        }

        for (FactoryCreator<?, R> factoryCreator : factoryCreators) {
            factoryCreator.fillFromExistingFactoryTree(classToFactory);
        }

        //.stream().allMatch(FactoryCreator::isEmpty);
    }

    @SuppressWarnings("unchecked")
     <F extends FactoryBase<?,R>> F getNew(Class<F> clazz){
        F result = factoryCreators.stream().filter(fc -> fc.match(clazz)).findAny().map(rFactoryCreator -> (F) rFactoryCreator.createNew(this)).orElse(null);
        if (result==null){
            throw new IllegalStateException("builder missing Factory: "+clazz);
        }
        return result;
    }

    public void reset() {
        for (FactoryCreator<?, R> creator : factoryCreators) {
            creator.reset();
        }
    }

    public <F extends FactoryBase<?, R>> void removeFactoryCreator(FactoryTemplateId<F> templateId) {
        this.factoryCreators.removeIf(rFactoryCreator -> rFactoryCreator.match(templateId.clazz, templateId.name));
    }
}
