package org.yuriey.minesweeper.message;

public interface Message<T> {
    T get();
    void set(T obj);
}
