package de.factoryfx.factory.datastorage;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import de.factoryfx.factory.FactoryBase;

/**
 * storage/load and history for factories
 *
 * @param <V> Visitor
 * @param <L> Root liveobject
 * @param <R> Root
 */
public interface FactoryStorage<V,L,R extends FactoryBase<L,V>> {

    R getHistoryFactory(String id);

    default R getPreviousHistoryFactory(String id) {
        Collection<StoredFactoryMetadata> historyFactoryList = getHistoryFactoryList();
        if (historyFactoryList.isEmpty())
            return null;
        List<StoredFactoryMetadata> historyFactoryListSorted = historyFactoryList.stream().sorted(Comparator.comparing(h -> h.creationTime)).collect(Collectors.toList());
        for (int i=0;i<historyFactoryListSorted.size();i++) {
            if (historyFactoryListSorted.get(i).id.equals(id) && i-1>=0) {
                return getHistoryFactory(historyFactoryListSorted.get(i - 1).id);
            }
        }
        return null;
    }

    Collection<StoredFactoryMetadata> getHistoryFactoryList();

    FactoryAndStoredMetadata<R> getCurrentFactory();

    /** prepare a new Factory which could we an update. mainly give it the correct baseVersionId*/
    FactoryAndNewMetadata<R> getPrepareNewFactory();

    /** updateCurrentFactory and history*/
    void  updateCurrentFactory(FactoryAndNewMetadata<R> update, String user, String comment);

    /**at Application start load current Factory*/
    void loadInitialFactory();
}
