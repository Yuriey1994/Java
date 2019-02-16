package org.yuriey.minesweeper.controller;

import javafx.beans.value.ChangeListener;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import org.yuriey.minesweeper.GlobalConfig;
import org.yuriey.minesweeper.SceneManager;
import org.yuriey.minesweeper.MineSweeper;
import org.yuriey.minesweeper.listener.SweepListener;
import org.yuriey.minesweeper.message.GameModeSetMessage;
import org.yuriey.minesweeper.message.Message;
import org.yuriey.minesweeper.model.CubeState;
import org.yuriey.minesweeper.model.EasyMode;
import org.yuriey.minesweeper.model.GameMode;
import org.yuriey.minesweeper.model.Cube;
import org.yuriey.minesweeper.service.TimeUpdater;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
/**
 * 游戏界面控制器
 */
public class GameSceneController implements Controller<Message<GameMode>> {
    @FXML
    private Label minesLabel;
    @FXML
    private Label timerLabel;
    @FXML
    private StackPane restartPane;
    @FXML
    private StackPane changeModePane;
    @FXML
    private StackPane rankPane;
    @FXML
    private GridPane minesArea;

    @FXML
    private GridPane gameGridPane;

    private List<MouseButton> currentPressedButton = new ArrayList<>();
    private MineSweeper sweepMine;
    private Pane[][] mineCubes;
    private boolean gameFailed;
    private GameMode gameMode = new EasyMode();
    private final GameSceneMouseEventHandler gameSceneMouseEventHandler = new GameSceneMouseEventHandler();
    private final ChangeListener<Number> minesAreaResizeListener = (observable, oldValue, newValue) -> GameSceneController.this.resizeMinesNumFont();
    private TimeUpdater timeUpdater;

    private SweepListener sweepListener = new SweepListener() {
        @Override
        public void sweeping(Cube cube) {

        }

        @Override
        public void swept(Cube cube) {
            if (cube.getState() == CubeState.EXCLUSIVE) {
                Pane mineGrid = mineCubes[cube.getPosition().getRowIndex()][cube.getPosition().getColIndex()];
                mineGrid.getStyleClass().removeAll(mineGrid.getStyleClass());
                mineGrid.getStyleClass().add("grid-cube-swept");
                ((Label) mineGrid.getChildren().get(0)).setText(cube.getMinesCountAround() == 0 ? "" : "" + cube.getMinesCountAround());
                if (cube.getMinesCountAround() > 0) {
                    ((Label) mineGrid.getChildren().get(0)).getStyleClass().removeAll(((Label) mineGrid.getChildren().get(0)).getStyleClass());
                    ((Label) mineGrid.getChildren().get(0)).getStyleClass().add("num-mines-" + cube.getMinesCountAround());
                }
            }
        }

        @Override
        public void failed(Cube explodedCube) {
            System.out.println("game over");
            gameFailed = true;
            showAllMines();
        }

        @Override
        public void finished() {
            System.out.println("finished!");
            showAllMines();
        }

        @Override
        public void onStateChange(Cube cube, CubeState oldState, CubeState newState) {

        }
    };

    private void showAllMines() {
        for (int i = 0; i < gameMode.getRowCount(); i++) {
            for (int j = 0; j < gameMode.getColumnCount(); j++) {
                Pane cube = mineCubes[i][j];
                if (sweepMine.getState(i, j) == CubeState.EXPLODED) {
                    cube.getStyleClass().removeAll(cube.getStyleClass());
                    cube.getStyleClass().add("grid-mine-exploded");
                } else if (sweepMine.isMine(i, j)) {
                    cube.getStyleClass().removeAll(cube.getStyleClass());
                    cube.getStyleClass().add("grid-mine");
                }

            }
        }

    }

    private void resizeMinesNumFont(double size) {
        for (Node child : minesArea.getChildren()) {
            Label label = ((Label) ((StackPane) child).getChildren().get(0));
            label.setFont(Font.font(size));
        }
    }

    private void resizeMinesNumFont() {
        double fontSize = minesArea.getWidth() * 0.5 / minesArea.getColumnCount();
        resizeMinesNumFont(fontSize);
    }

    private Node createMineItem() {
        Label label = new Label("");
        label.setAlignment(Pos.CENTER);
        label.setMouseTransparent(true);
        label.setFont(Font.font(12));
        StackPane sp = new StackPane(label);
        sp.getStyleClass().add("grid-cube");
        GridPane.setMargin(sp, new Insets(2, 2, 2, 2));
        sp.addEventHandler(MouseEvent.ANY, gameSceneMouseEventHandler);
        return sp;
    }

    @Override
    public void update(Message<GameMode> message) {
        if (message != null) this.gameMode = message.get();
        double heightDivWidth = gameMode.getColumnCount() * 1.0 / gameMode.getRowCount();
        double oldMinesAreaWidth = minesArea.getPrefWidth();
        double oldMinesAreaHeight = minesArea.getPrefHeight();
        double newMinesAreaWidth = oldMinesAreaHeight * gameMode.getColumnCount() / gameMode.getRowCount();
        gameGridPane.setPrefWidth(gameGridPane.getPrefWidth() + newMinesAreaWidth - oldMinesAreaWidth);
        minesArea.setPrefWidth(newMinesAreaWidth);
        gameGridPane.getColumnConstraints().get(1).setPercentWidth(minesArea.getPrefWidth() / gameGridPane.getPrefWidth() * 100);
        RowConstraints rowConstraints = minesArea.getRowConstraints().get(0);
        ColumnConstraints columnConstraints = minesArea.getColumnConstraints().get(0);
        minesArea.getChildren().removeAll(minesArea.getChildren());
        while (minesArea.getRowCount() < gameMode.getRowCount()) {
            minesArea.getRowConstraints().add(rowConstraints);
        }
        while (minesArea.getColumnCount() < gameMode.getColumnCount()) {
            minesArea.getColumnConstraints().add(columnConstraints);
        }

        for (int i = 0; i < minesArea.getColumnCount(); i++) {
            for (int j = 0; j < minesArea.getRowCount(); j++) {
                //StackPane child = ((StackPane) SceneManager.getInstance().loadNodeFromFXML("item-mine.fxml"));
                Node node = createMineItem();
                minesArea.add(node, i, j);
            }
        }
        //minesArea.setPrefWidth(minesArea.getPrefHeight() * heightDivWidth);
        SceneManager.getInstance().resizeInner();
        sweepMine = new MineSweeper(gameMode);
        initMineCubes();
        sweepMine.setSweepListener(sweepListener);
        timeUpdater = new TimeUpdater();
        timeUpdater.init(sweepMine, timerLabel);
        timeUpdater.start();
    }

    @Override
    public void onApplicationStop() {
        timeUpdater.terminate();
    }

    private void initMineCubes() {
        mineCubes = new Pane[gameMode.getRowCount()][gameMode.getColumnCount()];
        for (Node child : minesArea.getChildren()) {
            mineCubes[GridPane.getRowIndex(child)][GridPane.getColumnIndex(child)] = (Pane) child;
        }
    }

    enum ButtonAction {
        NONE,
        SWEEP_MINE,
        AUTO_SWEEP_MINE,
        MARK,
        MENU_RESTART,
        MENU_CHANGE_MODE,
        MENU_RANK
    }

    class GameSceneMouseEventHandler implements EventHandler<MouseEvent> {
        private ButtonAction buttonAction = ButtonAction.NONE;

        private List<Pane> getAroundInitialPane(Pane centerPane) {
            //获取中心方块周围的未被扫除和标记的方块
            List<Pane> aroundPanes = new ArrayList<>();
            int centerRowIndex = GridPane.getRowIndex(centerPane);
            int centerColIndex = GridPane.getColumnIndex(centerPane);
            for (int row = centerRowIndex - 1; row <= centerRowIndex + 1; row++) {
                for (int col = centerColIndex - 1; col <= centerColIndex + 1; col++) {
                    if (col >= 0 && row >= 0 && col < gameMode.getColumnCount() && row < gameMode.getRowCount()) {//if positon is valid
                        if ((row != centerRowIndex || col != centerColIndex) && sweepMine.getState(row, col) == CubeState.INITIAL)
                            aroundPanes.add(mineCubes[row][col]);//exclusive it self
                    }
                }
            }
            return aroundPanes;
        }

        private void pressAction(Pane pane) {
            //方块按下渲染
            if (buttonAction == ButtonAction.AUTO_SWEEP_MINE) {
                //中键或多建按下不设置按下方块的pressed效果，设置周围8个初始化状态的方块为Press状态
                cubeEffect(pane, false);
                for (Pane aroundPane : getAroundInitialPane(pane)) {
                    cubeEffect(aroundPane, true);
                }
            } else {
                cubeEffect(pane, true);
            }
        }

        private void releaseAction(Pane pane) {
            switch (buttonAction) {
                case AUTO_SWEEP_MINE:
                    sweepMine.checkMarkedMineAndSweepAround(GridPane.getRowIndex(pane), GridPane.getColumnIndex(pane));
                    //按压的
                    for (Pane aroundPane : getAroundInitialPane(pane)) {
                        cubeEffect(aroundPane, false);
                    }
                    buttonAction = ButtonAction.NONE;
                    break;
                case MARK:
                    switch (sweepMine.getState(GridPane.getRowIndex(pane), GridPane.getColumnIndex(pane))) {
                        case INITIAL:
                            sweepMine.setState(GridPane.getRowIndex(pane), GridPane.getColumnIndex(pane), CubeState.MARK_MINE);
                            break;
                        case MARK_MINE:
                            sweepMine.setState(GridPane.getRowIndex(pane), GridPane.getColumnIndex(pane), CubeState.MARK_UNKNOWN);
                            break;
                        case MARK_UNKNOWN:
                            sweepMine.setState(GridPane.getRowIndex(pane), GridPane.getColumnIndex(pane), CubeState.INITIAL);
                            break;
                    }
                    buttonAction = ButtonAction.NONE;
                    break;
                case SWEEP_MINE:
                    sweepMine.sweep(GridPane.getRowIndex(pane), GridPane.getColumnIndex(pane));
                    sweepMine.print();
                    buttonAction = ButtonAction.NONE;
                    break;
                case NONE:
                    break;
            }
            cubeEffect(pane, false);
        }

        private void cubeEffect(Pane pane, boolean pressed) {
            //获取方块状态决定渲染方式
            if (gameFailed) return;
            int markedCount = sweepMine.getMarkedMineCount();
            minesLabel.setText(markedCount + "/" + gameMode.getMines());
            String suffix = pressed ? "-pressed" : "";
            switch (sweepMine.getState(GridPane.getRowIndex(pane), GridPane.getColumnIndex(pane))) {
                case INITIAL:
                    pane.getStyleClass().removeAll(pane.getStyleClass());
                    pane.getStyleClass().add("grid-cube" + suffix);
                    break;
                case MARK_MINE:
                    pane.getStyleClass().removeAll(pane.getStyleClass());
                    pane.getStyleClass().add("grid-cube-mark" + suffix);
                    break;
                case MARK_UNKNOWN:
                    pane.getStyleClass().removeAll(pane.getStyleClass());
                    pane.getStyleClass().add("grid-cube-mark-unknown" + suffix);
                    break;
            }
        }

        @Override
        public void handle(MouseEvent event) {
            StackPane pane = ((StackPane) event.getTarget());
            if (event.getEventType() == MouseEvent.MOUSE_CLICKED) {
                if (pane == restartPane) {
                    SceneManager.getInstance().changeNodeOfContainer(GlobalConfig.FXML_GAME, new GameModeSetMessage(GameSceneController.this.gameMode));
                    timeUpdater.terminate();
                } else if (pane == changeModePane) {
                    SceneManager.getInstance().changeNodeOfContainer(GlobalConfig.FXML_MODE, null);
                    timeUpdater.terminate();
                } else if (pane == rankPane) {
                    timeUpdater.terminate();
                }

            } else if (event.getEventType() == MouseEvent.MOUSE_PRESSED) {//按下键判断响应类型
                if (pane != restartPane && pane != changeModePane && pane != rankPane) {
                    if (!currentPressedButton.contains(event.getButton())) currentPressedButton.add(event.getButton());
                    if (currentPressedButton.size() <= 1) {//按下1一个键
                        if (event.getButton() == MouseButton.PRIMARY) {
                            buttonAction = ButtonAction.SWEEP_MINE;
                        } else if (event.getButton() == MouseButton.SECONDARY) {
                            buttonAction = ButtonAction.MARK;
                        } else if (event.getButton() == MouseButton.MIDDLE) {
                            buttonAction = ButtonAction.AUTO_SWEEP_MINE;
                        }
                    } else {//按键超过1个
                        buttonAction = ButtonAction.AUTO_SWEEP_MINE;
                    }
                    pressAction(pane);
                }
            } else if (event.getEventType() == MouseEvent.MOUSE_RELEASED) {
                if (pane != restartPane && pane != changeModePane && pane != rankPane) {
                    if (currentPressedButton.contains(event.getButton()))
                        currentPressedButton.remove(event.getButton());
                    releaseAction(pane);
                }
            }
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        restartPane.addEventHandler(MouseEvent.ANY, gameSceneMouseEventHandler);
        changeModePane.addEventHandler(MouseEvent.ANY, gameSceneMouseEventHandler);
        rankPane.addEventHandler(MouseEvent.ANY, gameSceneMouseEventHandler);
        minesArea.widthProperty().addListener(minesAreaResizeListener);
        minesArea.widthProperty().addListener(minesAreaResizeListener);
    }
}
