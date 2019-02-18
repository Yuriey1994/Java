package org.yuriey.minesweeper.controller;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import org.yuriey.minesweeper.GlobalConfig;
import org.yuriey.minesweeper.SceneManager;
import org.yuriey.minesweeper.message.GameModeSetMessage;
import org.yuriey.minesweeper.message.MessageType;
import org.yuriey.minesweeper.model.CustomMode;
import org.yuriey.minesweeper.model.GameMode;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * 自定义模式界面控制器
 */
public class CustomSceneController implements Controller {
    @FXML
    private TextField widthTF;
    @FXML
    private TextField heightTF;
    @FXML
    private TextField minesTF;
    @FXML
    private Slider widthSlider;
    @FXML
    private Slider heightSlider;
    @FXML
    private Slider minesSlider;
    @FXML
    private Button backBtn;
    @FXML
    private Button startBtn;
    @FXML
    private Label maxMinesLabel;

    private final Integer minWidth = 8;
    private final Integer maxWidth = 30;
    private final Integer minHeight = 8;
    private final Integer maxHeight = 24;
    private final Integer minMines = 10;
    private Integer maxMines;

    private Integer curWidth;
    private Integer curHeight;
    private Integer curMines;

    @Override
    public void update(Object obj) {
        SceneManager.getInstance().resizeInner();
    }

    @Override
    public void onApplicationStop() {

    }
    private Integer computeWidth(double percent){
        return minWidth + (int) Math.round((maxWidth - minWidth) * percent / 100.0);
    }
    private Integer computeHeight(double percent){
        return minHeight + (int) Math.round((maxHeight - minHeight) * percent / 100.0);
    }
    private Integer computeMines(double percent){
        return minMines + (int) Math.round((maxMines - minMines) * percent / 100.0);
    }
    private Integer computeMaxMines(){
        return (curWidth-1)*(curHeight-1);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        curHeight = computeHeight(heightSlider.getValue());
        curWidth = computeWidth(widthSlider.getValue());
        maxMines = computeMaxMines();
        curMines = computeMines(minesSlider.getValue());
        widthTF.setText(String.valueOf(curWidth));
        heightTF.setText(String.valueOf(curHeight));
        minesTF.setText(String.valueOf(curMines));
        maxMinesLabel.setText(String.valueOf(maxMines));
        widthSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            curWidth = computeWidth(newValue.doubleValue());
            maxMines = computeMaxMines();
            if(curMines > maxMines){
                curMines = maxMines;
                minesTF.setText(String.valueOf(curMines));
            }
            widthTF.setText(String.valueOf(curWidth));
            maxMinesLabel.setText(String.valueOf(maxMines));
            minesSlider.setValue((curMines-minMines) * 100.0 / (maxMines-minMines));
        });
        heightSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            curHeight = computeHeight(newValue.doubleValue());
            maxMines = computeMaxMines();
            if(curMines > maxMines) {
                curMines = maxMines;
                minesTF.setText(String.valueOf(curMines));
            }
            heightTF.setText(String.valueOf(curHeight));
            maxMinesLabel.setText(String.valueOf(maxMines));
            minesSlider.setValue((curMines-minMines) * 100.0 / (maxMines-minMines));
        });
        minesSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            curMines = computeMines(newValue.doubleValue());
            minesTF.setText(String.valueOf(curMines));
        });
        startBtn.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                SceneManager.getInstance().changeNodeOfContainer(GlobalConfig.FXML_GAME,new GameModeSetMessage(MessageType.GAME_NEW, new CustomMode(curHeight,curWidth,curMines)));
            }
        });
        backBtn.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                SceneManager.getInstance().changeNodeOfContainer(GlobalConfig.FXML_MODE,null);
            }
        });
    }
}
