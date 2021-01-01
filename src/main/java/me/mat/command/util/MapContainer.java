package me.mat.command.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;

public class MapContainer<K, V> {

    private final Map<K, V> map = new HashMap<>();

    public V get(K key) {
        return map.get(key);
    }

    public V getOrDefault(K key, V defaultValue) {
        return map.getOrDefault(key, defaultValue);
    }

    public V putIfAbsent(K key, V value) {
        return map.putIfAbsent(key, value);
    }

    public Set<Map.Entry<K, V>> entrySet() {
        return map.entrySet();
    }

    public void put(K key, V value) {
        map.put(key, value);
    }

    public void pop(K key) {
        map.remove(key);
    }

    public void clear() {
        map.clear();
    }

    public void forEach(BiConsumer<? super K, ? super V> action) {
        map.forEach(action);
    }

    public boolean hasKey(K key) {
        return map.containsKey(key);
    }

    public boolean hasValue(V value) {
        return map.containsValue(value);
    }

    public boolean empty() {
        return map.isEmpty();
    }

    public int size() {
        return map.size();
    }

}
