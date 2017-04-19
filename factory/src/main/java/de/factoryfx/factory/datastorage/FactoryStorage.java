package de.factoryfx.factory.datastorage;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import de.factoryfx.factory.FactoryBase;

public interface FactoryStorage<L,V,T extends FactoryBase<L,V>> {

    T getHistoryFactory(String id);

    default T getPreviousHistoryFactory(String id) {
        Collection<StoredFactoryMetadata> historyFactoryList = getHistoryFactoryList();
        if (historyFactoryList.isEmpty())
            return null;
        List<StoredFactoryMetadata> metadata = historyFactoryList.stream().sorted(Comparator.comparing(h -> h.creationTime)).collect(Collectors.toList());
        String lastId = metadata.get(0).id;
        for (StoredFactoryMetadata m : metadata) {
            if (m.id.equals(id))
                return getHistoryFactory(lastId);
            lastId = m.id;
        }
        return null;
    }

    Collection<StoredFactoryMetadata> getHistoryFactoryList();

    FactoryAndStoredMetadata<T> getCurrentFactory();

    /** prepare a new Factory which could we an update. mainly give it the correct baseVersionId*/
    FactoryAndNewMetadata<T> getPrepareNewFactory();

    /** updateCurrentFactory and history*/
    void  updateCurrentFactory(FactoryAndNewMetadata<T> update, String user, String comment);

    /**at Application start load current Factory*/
    void loadInitialFactory();
}
