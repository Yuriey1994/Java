package org.yuriey.minesweeper.model;

public interface GameMode {
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

    Integer getRowCount();

    public Integer getColumnCount();

    public Integer getMines();
}
