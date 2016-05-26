package de.factoryfx.model.jackson;

import java.util.Collection;
import java.util.Iterator;
import java.util.TreeSet;

import javafx.beans.InvalidationListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;

public class ObservableSetJacksonAbleWrapper<T> implements ObservableSet<T> {

    private final ObservableSet<T> observableSet;

    public ObservableSetJacksonAbleWrapper() {
        this.observableSet = FXCollections.observableSet(new TreeSet<>());
    }

    @Override
    public void addListener(SetChangeListener<? super T> listener) {
        observableSet.addListener(listener);
    }

    @Override
    public void removeListener(SetChangeListener<? super T> listener) {
        observableSet.removeListener(listener);
    }

    @Override
    public void addListener(InvalidationListener listener) {
        observableSet.addListener(listener);
    }

    @Override
    public void removeListener(InvalidationListener listener) {
        observableSet.addListener(listener);
    }

    @Override
    public int hashCode() {
        return observableSet.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        return observableSet.equals(o);
    }

    @Override
    public int size() {
        return observableSet.size();
    }

    @Override
    public boolean isEmpty() {
        return observableSet.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return observableSet.contains(o);
    }

    @Override
    public Iterator<T> iterator() {
        return observableSet.iterator();
    }

    @Override
    public Object[] toArray() {
        return observableSet.toArray();
    }

    @Override
    public <E> E[] toArray(E[] a) {
        return observableSet.toArray(a);
    }

    @Override
    public boolean add(T t) {
        return observableSet.add(t);
    }

    @Override
    public boolean remove(Object o) {
        return observableSet.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return observableSet.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        return observableSet.addAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return observableSet.retainAll(c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return observableSet.removeAll(c);
    }

    @Override
    public void clear() {
        observableSet.clear();
    }

    public ObservableSet<T> unwrap() {
        return observableSet;
    }

}
