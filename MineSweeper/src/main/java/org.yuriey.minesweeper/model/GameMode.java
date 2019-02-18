package org.yuriey.minesweeper.model;

import java.io.Serializable;

public abstract class GameMode implements Serializable {
//    private Integer gridWidth = 8;
//    private Integer gridHeight = 8;
//    private Integer mines = 10;

//    public GameMode() {
//    }
//
//    public GameMode(Integer gridWidth, Integer gridHeight, Integer mines) {
//        this.gridWidth = gridWidth;
//        this.gridHeight = gridHeight;
//        this.mines = mines;
//    }

    abstract public Integer getRowCount();

    abstract public Integer getColumnCount();

    abstract public Integer getMineCount();

    @Override
    public String toString() {
        return String.format("%1$d * %2$d  %3$d mines",getColumnCount(),getRowCount(), getMineCount());
    }

    @Override
    public boolean equals(Object obj) {
        return toString().equals(obj.toString());
    }
}
