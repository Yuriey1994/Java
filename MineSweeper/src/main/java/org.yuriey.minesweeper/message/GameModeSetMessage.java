package org.yuriey.minesweeper.message;

import org.yuriey.minesweeper.model.GameMode;

public class GameModeSetMessage implements Message<GameMode> {
    private GameMode gameMode;

    public GameModeSetMessage(GameMode gameMode) {
        this.gameMode = gameMode;
    }

    @Override
    public GameMode get() {
        return gameMode;
    }

    @Override
    public void set(GameMode obj) {
        this.gameMode = gameMode;
    }
}
