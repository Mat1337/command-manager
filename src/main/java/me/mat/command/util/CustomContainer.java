package me.mat.command.util;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class CustomContainer<T> {

    private final List<T> list = new CopyOnWriteArrayList<>();

    public Stream<T> filter(Predicate<? super T> predicate) {
        return stream().filter(predicate);
    }

    public Stream<T> stream() {
        return list.stream();
    }

    public Optional<T> find(Predicate<? super T> predicate) {
        return filter(predicate).findFirst();
    }

    public Optional<T> findByClass(Class<?> aClass) {
        return find(t -> t.getClass() == aClass);
    }

    public void forEach(Consumer<? super T> action) {
        list.forEach(action);
    }

    public boolean removeIf(Predicate<? super T> filter) {
        return list.removeIf(filter);
    }

    public void add(T... items) {
        Arrays.stream(items).forEach(this::add);
    }

    public void add(T item) {
        list.add(item);
    }

    public void remove(T item) {
        list.remove(item);
    }

    public boolean has(T item) {
        return list.contains(item);
    }

    public boolean isEmpty() {
        return list.isEmpty();
    }

    public int size() {
        return list.size();
    }

}
