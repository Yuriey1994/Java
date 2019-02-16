package org.yuriey.minesweeper.model;

public class HardMode implements GameMode {
    private final Integer rowCount = 16;
    private final Integer columnCount = 30;
    private final Integer mines = 99;
    @Override
    public Integer getRowCount() {
        return rowCount;
    }

    @Override
    public Integer getColumnCount() {
        return columnCount;
    }

    @Override
    public Integer getMines() {
        return mines;
    }
}
