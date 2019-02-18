package org.yuriey.minesweeper.model;

public class Cube {
    private CubeState state;
    private boolean mine;
    private CubePosition position;
    private int minesCountAround;

    public Cube() {
        this(CubeState.INITIAL, false, 0, new CubePosition());
    }

    public Cube(CubeState state, boolean mine, int minesCountAround, CubePosition position) {
        this.state = state;
        this.mine = mine;
        this.position = position;
        this.minesCountAround = minesCountAround;
    }

    public int getMinesCountAround() {
        return minesCountAround;
    }

    public void setMinesCountAround(int minesCountAround) {
        this.minesCountAround = minesCountAround;
    }

    public CubeState getState() {
        return state;
    }

    public void setState(CubeState state) {
        this.state = state;
    }

    public boolean isMine() {
        return mine;
    }

    public void setMine(boolean mine) {
        this.mine = mine;
    }

    public CubePosition getPosition() {
        return position;
    }

    public void setPosition(CubePosition position) {
        this.position = position;
    }
}
