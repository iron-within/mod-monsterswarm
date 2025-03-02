package com.ironwithin.monsterswarm.data;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class ValueMap<K> {
    private final HashMap<K, Value> values;
    private Iterator<Map.Entry<K, Value>> iterator;

    public ValueMap(int capacity) {
        this.values = new HashMap<>(capacity, 0.9F);
    }

    public interface ReduceObserver<K> {
        void removed(K key);
    }

    private static class Value {
        int val;
    }

    public boolean next() {
        if (!this.iterator.hasNext()) {
            return false;
        }
        Map.Entry<K, Value> next = this.iterator.next();
        K key = next.getKey();
        Value val = next.getValue();
        return true;
    }

    public void remove() {
        if (this.iterator != null) {
            this.iterator.remove();
        }
    }

    public void reduce(ReduceObserver<K> obs) {
        Iterator<Map.Entry<K, Value>> _iterator = this.values.entrySet().iterator();
        while (_iterator.hasNext()) {
            Map.Entry<K, Value> next = _iterator.next();
            Value v = next.getValue();
            if (--v.val < 1) {
                if (obs != null) {
                    obs.removed(next.getKey());
                }
                _iterator.remove();
            }
        }
    }

    public int remove(K key, int def) {
        Value v = this.values.remove(key);
        return (v == null) ? def : v.val;
    }

    public void put(K key, int value) {
        Value v = this.values.computeIfAbsent(key, k -> new Value());
        v.val = value;
    }

    public int increment(K key, int increment) {
        Value v = this.values.computeIfAbsent(key, k -> new Value());
        int old = v.val;
        v.val += increment;
        return old;
    }

    public int incrementExisting(K key, int increment) {
        Value v = this.values.get(key);
        if (v == null) {
            return -1;
        }
        int old = v.val;
        v.val += increment;
        return old;
    }

    public int size() {
        return this.values.size();
    }

    public void clear() {
        this.values.clear();
    }

    public Set<K> keys() {
        return this.values.keySet();
    }
}
