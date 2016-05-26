package de.factoryfx.factory.jackson;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import javafx.beans.InvalidationListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

public class ObservableListJacksonAbleWrapper<T> implements ObservableList<T> {

    private final ObservableList<T> observableList;

    public ObservableListJacksonAbleWrapper() {
        this.observableList = FXCollections.observableArrayList();
    }

    @Override
    public void addListener(ListChangeListener<? super T> listener) {
        observableList.addListener(listener);
    }

    @Override
    public void removeListener(ListChangeListener<? super T> listener) {
        observableList.removeListener(listener);
    }

    @SafeVarargs
    @Override
    public final boolean addAll(T... elements) {
        return observableList.addAll(elements);
    }

    @SafeVarargs
    @Override
    public final boolean setAll(T... elements) {
        return observableList.setAll(elements);
    }

    @Override
    public boolean setAll(Collection<? extends T> col) {
        return observableList.setAll(col);
    }

    @SafeVarargs
    @Override
    public final boolean removeAll(T... elements) {
        return observableList.removeAll(elements);
    }

    @SafeVarargs
    @Override
    public final boolean retainAll(T... elements) {
        return observableList.retainAll(elements);
    }

    @Override
    public void remove(int from, int to) {
        observableList.remove(from, to);
    }

    @Override
    public void addListener(InvalidationListener listener) {
        observableList.addListener(listener);
    }

    @Override
    public void removeListener(InvalidationListener listener) {
        observableList.addListener(listener);
    }

    @Override
    public int hashCode() {
        return observableList.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        return observableList.equals(o);
    }

    @Override
    public int size() {
        return observableList.size();
    }

    @Override
    public boolean isEmpty() {
        return observableList.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return observableList.contains(o);
    }

    @Override
    public Iterator<T> iterator() {
        return observableList.iterator();
    }

    @Override
    public Object[] toArray() {
        return observableList.toArray();
    }

    @Override
    public <E> E[] toArray(E[] a) {
        return observableList.toArray(a);
    }

    @Override
    public boolean add(T t) {
        return observableList.add(t);
    }

    @Override
    public boolean remove(Object o) {
        return observableList.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return observableList.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        return observableList.addAll(c);
    }

    @Override
    public boolean addAll(int index, Collection<? extends T> c) {
        return observableList.addAll(index, c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return observableList.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return observableList.retainAll(c);
    }

    @Override
    public void clear() {
        observableList.clear();
    }

    @Override
    public T get(int index) {
        return observableList.get(index);
    }

    @Override
    public T set(int index, T element) {
        return observableList.set(index, element);
    }

    @Override
    public void add(int index, T element) {
        observableList.add(index, element);
    }

    @Override
    public T remove(int index) {
        return observableList.remove(index);
    }

    @Override
    public int indexOf(Object o) {
        return observableList.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return observableList.lastIndexOf(o);
    }

    @Override
    public ListIterator<T> listIterator() {
        return observableList.listIterator();
    }

    @Override
    public ListIterator<T> listIterator(int index) {
        return observableList.listIterator(index);
    }

    @Override
    public List<T> subList(int fromIndex, int toIndex) {
        return observableList.subList(fromIndex, toIndex);
    }

    public ObservableList<T> unwrap() {
        return observableList;
    }
}
