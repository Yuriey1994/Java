package org.yuriey.minesweeper.service;


import javafx.application.Platform;
import javafx.scene.control.Label;
import org.yuriey.minesweeper.MineSweeper;

public class TimeUpdater extends Thread {
    private boolean stop = false;
    private Label label;
    private MineSweeper game;

    public void init(MineSweeper game, Label label) {
        this.label = label;
        this.game = game;
    }


    @Override
    public void run() {
        while (!stop) {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    if (label != null && game != null) {
                        int usedSeconds = game.getUsedSeconds();
                        int minte = usedSeconds / 60;
                        int seconds = usedSeconds % 60;
                        label.setText(String.valueOf(minte / 10) + String.valueOf(minte % 10) + ":" + String.valueOf(seconds / 10) + String.valueOf(seconds % 10));
                    }
                }
            });
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void terminate() {
        stop = true;
    }

    @Override
    public synchronized void start() {
        stop = false;
        super.start();
    }
}
