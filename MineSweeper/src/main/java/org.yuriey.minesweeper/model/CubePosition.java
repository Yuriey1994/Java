package org.yuriey.minesweeper.model;

public class CubePosition {
    private int colIndex;
    private int rowIndex;

    public CubePosition() {
    }

    public CubePosition(int rowIndex, int colIndex) {
        this.colIndex = colIndex;
        this.rowIndex = rowIndex;
    }

    public int getColIndex() {
        return colIndex;
    }

    public void setColIndex(int colIndex) {
        this.colIndex = colIndex;
    }

    public int getRowIndex() {
        return rowIndex;
    }

    public void setRowIndex(int rowIndex) {
        this.rowIndex = rowIndex;
    }

    @Override
    public String toString() {
        return "CubePosition{" +
                "rowIndex=" + rowIndex +
                ", colIndex=" + colIndex +
                '}';
    }
}
