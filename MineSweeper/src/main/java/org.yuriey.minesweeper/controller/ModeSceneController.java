package org.yuriey.minesweeper.controller;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import org.yuriey.minesweeper.SceneManager;
import org.yuriey.minesweeper.GlobalConfig;
import org.yuriey.minesweeper.message.GameModeSetMessage;
import org.yuriey.minesweeper.message.Message;
import org.yuriey.minesweeper.message.MessageType;
import org.yuriey.minesweeper.model.EasyMode;
import org.yuriey.minesweeper.model.GameMode;
import org.yuriey.minesweeper.model.HardMode;
import org.yuriey.minesweeper.model.MediumMode;

import java.net.URL;
import java.util.ResourceBundle;
/**
 * 模式选择界面控制器
 */
public class ModeSceneController implements Controller<Message<GameMode>> {

    @FXML
    private Pane easyModePane;
    @FXML
    private Pane mediumModePane;
    @FXML
    private Pane hardModePane;
    @FXML
    private Pane customModePane;
    @FXML
    private StackPane containerPane;

    private final ModeSceneMouseEventHandler modeSceneMouseEventHandler = new ModeSceneMouseEventHandler();

    @Override
    public void update(Message obj) {
        SceneManager.getInstance().resizeInner();
    }

    @Override
    public void onApplicationStop() {

    }


    class ModeSceneMouseEventHandler implements EventHandler<MouseEvent> {

        @Override
        public void handle(MouseEvent event) {
            Pane target = ((Pane) event.getTarget());
            if (event.getEventType() == MouseEvent.MOUSE_CLICKED) {
                switch (event.getButton()) {
                    case PRIMARY:
                        GameMode mode = null;
                        String fxml = GlobalConfig.FXML_GAME;
                        if (target == easyModePane) {
                            mode = new EasyMode();
                        } else if (target == mediumModePane) {
                            mode = new MediumMode();
                        } else if (target == hardModePane) {
                            mode = new HardMode();
                        } else if (target == customModePane) {
                            fxml = GlobalConfig.FXML_CUSTOM;
                            mode = null;
                        }
                        SceneManager.getInstance().changeNodeOfContainer(fxml, new GameModeSetMessage(MessageType.GAME_NEW, mode));
                        break;
                    default:
                        break;
                }
            }

        }
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        easyModePane.addEventHandler(MouseEvent.ANY, modeSceneMouseEventHandler);
        mediumModePane.addEventHandler(MouseEvent.ANY, modeSceneMouseEventHandler);
        hardModePane.addEventHandler(MouseEvent.ANY, modeSceneMouseEventHandler);
        customModePane.addEventHandler(MouseEvent.ANY, modeSceneMouseEventHandler);
    }
}
