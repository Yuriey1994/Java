package org.yuriey.minesweeper.model;

public class EasyMode extends GameMode {
    private final Integer rowCount = 8;
    private final Integer columnCount = 8;
    private final Integer mineCount = 10;
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
