package org.yuriey.minesweeper.listener;


import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import org.yuriey.minesweeper.SceneManager;

public class ContainerWidthChangeListener implements ChangeListener<Number> {
    @Override
    public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
        //SceneManager.getInstance().setContainerWidth(newValue.doubleValue());
        SceneManager.getInstance().resizeInner();
    }
}
