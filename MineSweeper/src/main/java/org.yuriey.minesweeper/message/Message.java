package org.yuriey.minesweeper.message;

public interface Message<T> {
    T getAttached();
    void setAttached(T obj);
    MessageType getType();
    void setType(MessageType type);
}
