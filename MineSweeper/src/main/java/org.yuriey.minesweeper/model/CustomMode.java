package org.yuriey.minesweeper.model;

public class CustomMode extends GameMode {
    private Integer rowCount;
    private Integer colCount;
    private Integer mineCount;

    public CustomMode(Integer rowCount, Integer colCount, Integer mineCount) {
        this.rowCount = rowCount;
        this.colCount = colCount;
        this.mineCount = mineCount;
    }

    @Override
    public Integer getRowCount() {
        return rowCount;
    }

    @Override
    public Integer getColumnCount() {
        return colCount;
    }

    @Override
    public Integer getMineCount() {
        return mineCount;
    }
}
