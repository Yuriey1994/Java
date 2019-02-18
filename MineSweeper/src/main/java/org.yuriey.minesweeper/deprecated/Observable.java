package org.yuriey.minesweeper.deprecated;

import org.yuriey.minesweeper.message.Message;

public interface Observable {
    boolean register(Observer observer);
    boolean cancel(Observer observer);
    void nontifyAll(Message msg);
    void notify(Observer observer, Message msg);
    void setChanged(boolean changed);
}
