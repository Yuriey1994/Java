package org.yuriey.minesweeper;

import javafx.animation.FadeTransition;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import org.yuriey.minesweeper.controller.Controller;
import org.yuriey.minesweeper.message.Message;

import java.io.IOException;
import java.net.URL;

/**
 * 管理各个界面的切换和缩放
 */
public class SceneManager {
    private static SceneManager sceneManager;
    private Pane container;
    private Pane currentInner;
    private Controller currentController;


    public Pane getCurrentInner() {
        return currentInner;
    }

    /**
     * 获取容器，该容器是一个Pane类，可以容纳所有场景
     * @return Pane类型的容器对象
     */
    public Pane getContainer() {
        return container;
    }

    public void setContainer(Pane container) {
        this.container = container;
    }

    private SceneManager() {
    }

    /**
     * 切换容器中容纳的节点
     * @param fxmlName 需要切换的场景的fxml文件名
     * @param msg 切换场景传递的消息
     * @param <T> 消息中包含的参数类型
     */
    public <T> void changeNodeOfContainer(String fxmlName, Message<T> msg) {
        try {
            //Parent root = FXMLLoader.load(getClass().getResource(fxmlName));//该方法无法获取到Controller实例
            URL location = getClass().getResource(fxmlName);
            System.out.println("location: " + location);
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(location);
            fxmlLoader.setBuilderFactory(new JavaFXBuilderFactory());
            Node node = fxmlLoader.load();
            Controller controller = fxmlLoader.getController();

            node.setOpacity(0.0f);
            container.getChildren().removeAll(container.getChildren());
            container.getChildren().add(node);

            //每次切换Scene都设置Fade过渡
            FadeTransition fadeTransition = new FadeTransition(Duration.millis(500), node);
            fadeTransition.setFromValue(0.0f);
            fadeTransition.setToValue(1.0f);
            fadeTransition.play();
            currentInner = ((Pane) node);
            currentController = controller;
            controller.update(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 容fxml文件中加载Node对象
     * @param fxml
     * @return
     */
    public Node loadNodeFromFXML(String fxml) {
        Node node = null;
        URL location = getClass().getResource(fxml);
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(location);
        fxmlLoader.setBuilderFactory(new JavaFXBuilderFactory());
        try {
            node = fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return node;
    }

    /**
     * 获取SceneManager实例
     * @return
     */
    public static SceneManager getInstance() {
        if (sceneManager == null) {
            synchronized (SceneManager.class) {
                if (sceneManager == null) {
                    sceneManager = new SceneManager();
                }
            }
        }
        return sceneManager;
    }

    /**
     * 根据原始Inner的宽高比缩放当前Inner
     */
    public void resizeInner() {
        double innerWidthDivHeight = currentInner.getPrefWidth() / currentInner.getPrefHeight();
        double containerValidWidth = container.getWidth() - (container.getPadding().getLeft() + container.getPadding().getRight());
        double containerValidHeight = container.getHeight() - (container.getPadding().getBottom() + container.getPadding().getTop());
        double containerValidScale = containerValidWidth / containerValidHeight;
        if (innerWidthDivHeight < containerValidScale) {
            currentInner.setPrefHeight(containerValidHeight);
            currentInner.setPrefWidth(containerValidHeight * innerWidthDivHeight);
        } else {
            currentInner.setPrefWidth(containerValidWidth);
            currentInner.setPrefHeight(containerValidWidth / innerWidthDivHeight);
        }
    }

    /**
     * 应用被关闭的时候调用
     */
    public void onApplicationStop() {
        if (currentController != null)
            currentController.onApplicationStop();
    }
}
