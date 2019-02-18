package org.yuriey.minesweeper.model;

public class MediumMode extends GameMode {
    private final Integer rowCount = 16;
    private final Integer columnCount = 16;
    private final Integer mineCount = 40;
    @Override
    public Integer getRowCount() {
        return rowCount;
    }

    @Override
    public Integer getColumnCount() {
        return columnCount;
    }

    @Override
    public Integer getMineCount() {
        return mineCount;
    }
}
