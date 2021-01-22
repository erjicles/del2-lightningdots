package com.delsquared.lightningdots.utilities;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class OrderedObjectCollection<T extends INamedObject> implements Iterable<T> {

    private List<String> listObjectNames;
    private Map<String, Integer> mapObjectIndexes;
    private Map<String, T> mapObjects;

    public void clear() {
        listObjectNames = new ArrayList<>();
        mapObjectIndexes = new HashMap<>();
        mapObjects = new HashMap<>();
    }

    public OrderedObjectCollection() {
        clear();
    }

    public OrderedObjectCollection(List<T> orderedObjectList) {
        clear();
        for (int i = 0; i < orderedObjectList.size(); i++) {
            T object= orderedObjectList.get(i);
            listObjectNames.add(object.getName());
            mapObjectIndexes.put(object.getName(), i);
            mapObjects.put(object.getName(), object);
        }
    }

    @SuppressWarnings("unused")
    public OrderedObjectCollection(
            List<String> listObjectNames
            , Map<String, T> mapObjects) {
        clear();
        for (int i = 0; i < listObjectNames.size(); i++) {
            String objectName = listObjectNames.get(i);
            mapObjectIndexes.put(objectName, i);
        }
        this.listObjectNames = listObjectNames;
        this.mapObjects = mapObjects;
    }

    @SuppressWarnings("unused")
    public OrderedObjectCollection(
            List<String> listObjectNames
            , Map<String, Integer> mapObjectIndexes
            , Map<String, T> mapObjects) {
        this.listObjectNames = listObjectNames;
        this.mapObjectIndexes = mapObjectIndexes;
        this.mapObjects = mapObjects;
    }

    public T getObject(String objectName) {
        if (mapObjects.containsKey(objectName)) {
            return mapObjects.get(objectName);
        }
        return null;
    }

    public T getObject(int index) {
        if (index >= 0 && index < listObjectNames.size()) {
            return getObject(listObjectNames.get(index));
        }
        return null;
    }

    public int getObjectIndex(String objectName) {
        if (mapObjectIndexes.containsKey(objectName)) {
            Integer result = mapObjectIndexes.get(objectName);
            if (result == null) {
                return -1;
            }
            return result;
        }
        return -1;
    }

    public String getObjectName(int index) {
        if (index >= 0 && index < listObjectNames.size()) {
            return listObjectNames.get(index);
        }
        return null;
    }

    public int size() {
        return mapObjects.size();
    }

    @NonNull
    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private int currentIndex = 0;
            @Override
            public boolean hasNext() {
                return currentIndex >= 0 && currentIndex < listObjectNames.size();
            }
            @Override
            public T next() {
                return getObject(currentIndex++);
            }
            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

}
