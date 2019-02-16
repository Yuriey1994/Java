package org.yuriey.minesweeper.model;

public class EasyMode implements GameMode {
    private final Integer rowCount = 8;
    private final Integer columnCount = 8;
    private final Integer mines = 10;
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
