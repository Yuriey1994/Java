package org.yuriey.minesweeper.model;

import java.io.Serializable;

public class Rank implements Serializable {
    private String name;
    private Integer usedSeconds;
    private GameMode gameMode;

    public Rank(String name, Integer usedSeconds, GameMode gameMode) {
        this.name = name;
        this.usedSeconds = usedSeconds;
        this.gameMode = gameMode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getUsedSeconds() {
        return usedSeconds;
    }

    public void setUsedSeconds(Integer usedSeconds) {
        this.usedSeconds = usedSeconds;
    }

    public GameMode getGameMode() {
        return gameMode;
    }

    public void setGameMode(GameMode gameMode) {
        this.gameMode = gameMode;
    }

    @Override
    public String toString() {
        return String.format("%-25s%-5dseconds", name, usedSeconds);
//        return "Rank{" +
//                "name='" + name + '\'' +
//                ", usedSeconds=" + usedSeconds +
//                ", gameMode=" + gameMode +
//                '}';
    }
}
