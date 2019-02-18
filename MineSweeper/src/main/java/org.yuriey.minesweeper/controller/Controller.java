package org.yuriey.minesweeper.controller;

import javafx.fxml.Initializable;
import org.yuriey.minesweeper.message.Message;

public interface Controller<T> extends Initializable {
    void update(T obj);
    void onApplicationStop();
}
