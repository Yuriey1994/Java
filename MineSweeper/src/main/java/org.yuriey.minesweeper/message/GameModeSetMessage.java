package org.yuriey.minesweeper.message;

import org.yuriey.minesweeper.model.GameMode;

public class GameModeSetMessage implements Message<GameMode> {
    private GameMode gameMode;
    private MessageType messageType = MessageType.NONE;

    public GameModeSetMessage(MessageType type, GameMode gameMode) {
        this.messageType = type;
        this.gameMode = gameMode;
    }

    @Override
    public GameMode getAttached() {
        return gameMode;
    }

    @Override
    public void setAttached(GameMode obj) {
        this.gameMode = gameMode;
    }

    @Override
    public MessageType getType() {
        return messageType;
    }

    @Override
    public void setType(MessageType type) {
        this.messageType=type;
    }
}
