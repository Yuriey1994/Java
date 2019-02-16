package org.yuriey.minesweeper.deprecated;

import org.yuriey.minesweeper.message.Message;

import java.util.ArrayList;
import java.util.List;

public abstract class Subject implements Observable {
    private String subjectName;
    private boolean changed = false;
    private List<Observer> observers = new ArrayList<>();

    public Subject(String subjectName) {
        this.subjectName = subjectName;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    @Override
    public void setChanged(boolean changed) {
        this.changed = changed;
    }

    @Override
    public boolean register(Observer observer) {
        return observers.add(observer);
    }

    @Override
    public boolean cancel(Observer observer) {
        return observers.remove(observer);
    }

    @Override
    public void nontifyAll(Message msg) {
        if(!changed)return;
        for (Observer o : observers) {
            o.update(msg);
        }
    }

    @Override
    public void notify(Observer observer, Message msg) {
        if(!changed)return;
        observer.update(msg);
    }
}
