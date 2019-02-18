package org.yuriey.minesweeper.controller;

import javafx.fxml.FXML;
import javafx.scene.layout.StackPane;
import org.yuriey.minesweeper.SceneManager;
import org.yuriey.minesweeper.GlobalConfig;
import org.yuriey.minesweeper.listener.ContainerHeightChangeListener;
import org.yuriey.minesweeper.listener.ContainerWidthChangeListener;
import org.yuriey.minesweeper.message.Message;
import org.yuriey.minesweeper.model.GameMode;

import java.net.URL;
import java.util.ResourceBundle;
/**
 * 容器界面控制器
 */
public class MainSceneController implements Controller<Message<GameMode>> {
    @FXML
    private StackPane container;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        SceneManager.getInstance().setContainer(container);
        SceneManager.getInstance().changeNodeOfContainer(GlobalConfig.FXML_MODE, null);
        container.widthProperty().addListener(new ContainerWidthChangeListener());
        container.heightProperty().addListener(new ContainerHeightChangeListener());
    }

    @Override
    public void update(Message msg) {

    }

    @Override
    public void onApplicationStop() {

    }
}
