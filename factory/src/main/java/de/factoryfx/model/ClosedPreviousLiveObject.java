package de.factoryfx.model;

import java.util.List;
import java.util.Map;

public class ClosedPreviousLiveObject<E extends LiveObject> {

    Map<String,LiveObject> lifeObjects;

    public interface ClosedPreviousLiveObjectVisitor<E extends LiveObject>{
        void nothing();
        void single(E closedPreviousLifeObject);
        void multiple(List<E> closedPreviousLifeObjects);
    }

    public void visit(ClosedPreviousLiveObjectVisitor<E> closedPreviousLiveObjectVisitor){

    }
}
