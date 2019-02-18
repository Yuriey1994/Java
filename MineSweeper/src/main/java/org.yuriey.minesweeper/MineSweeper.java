package org.yuriey.minesweeper;

import org.yuriey.minesweeper.listener.SweepListener;
import org.yuriey.minesweeper.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MineSweeper {
    private Cube[][] cubes;
    private GameMode mode;
    private boolean failed;
    private boolean finished;
    private int sweptCount = 0;
    private boolean generated;
    private Integer usedSeconds;
    private long gameStartTimeMills = -1;
    private static MineSweeper singletonMineSweeper;

    public boolean isFailed() {
        return failed;
    }

    public boolean isFinished() {
        return finished;
    }

    public static MineSweeper getSingletonMineSweeper() {
        if (singletonMineSweeper == null) {
            synchronized (MineSweeper.class) {
                if(singletonMineSweeper == null) {
                    singletonMineSweeper = new MineSweeper(new EasyMode());
                }
            }
        }
        return singletonMineSweeper;
    }

    public MineSweeper reset(){
        failed =false;
        finished = false;
        usedSeconds = 0;
        sweptCount = 0;
        gameStartTimeMills = -1;
        for (int i = 0; i < cubes.length; i++) {
            for (int j = 0; j < cubes[i].length; j++) {
                cubes[i][j].setState(CubeState.INITIAL);
            }
        }
        return this;
    }
    public MineSweeper newGame(){
        cubes = new Cube[mode.getRowCount()][mode.getColumnCount()];
        failed = false;
        finished = false;
        usedSeconds = 0;
        sweptCount = 0;
        gameStartTimeMills = -1;
        this.generated = false;
        for (int i = 0; i < mode.getRowCount(); i++) {
            for (int j = 0; j < mode.getColumnCount(); j++) {
                cubes[i][j] = new Cube(CubeState.INITIAL, false, 0, new CubePosition(i, j));
            }
        }
        return this;
    }
    public MineSweeper newGame(GameMode mode) {
        this.mode = mode;
        return newGame();
    }

    public Integer getUsedSeconds() {
        if (failed || finished) return usedSeconds;
        usedSeconds = (gameStartTimeMills == -1 ? 0 : ((int) ((System.currentTimeMillis() - gameStartTimeMills) / 1000)));
        return usedSeconds;
    }

    private SweepListener listener = new SweepListener() {
        @Override
        public void sweeping(Cube cube) {
            System.out.println("sweeping pos=" + cube.getPosition());
        }

        @Override
        public void swept(Cube cube) {
            System.out.println("swept pos=" + cube.getPosition());
        }

        @Override
        public void failed(Cube explodedCube) {
            System.out.println("failed exploded cube=" + explodedCube.getPosition());
        }

        @Override
        public void finished() {
            System.out.println("finished!");
        }

        @Override
        public void onStateChange(Cube cube, CubeState oldState, CubeState newState) {

        }
    };

    public boolean isMine(int rowIndex, int colIndex) {
        return getCube(rowIndex, colIndex) == null ? false : getCube(rowIndex, colIndex).isMine();
    }

    public int getMarkedMineCount() {
        int count = 0;
        for (int i = 0; i < mode.getRowCount(); i++) {
            for (int j = 0; j < mode.getColumnCount(); j++) {
                if (cubes[i][j].getState() == CubeState.MARK_MINE) count++;
            }
        }
        return count;
    }

    public CubeState getState(int rowIndex, int colIndex) {
        return isPositionValid(rowIndex, colIndex) ? cubes[rowIndex][colIndex].getState() : null;
    }

    public MineSweeper(GameMode mode) {
        this.mode = mode;
        newGame();
    }

    public void setSweepListener(SweepListener listener) {
        this.listener = listener;
    }


    private void generateMines(int rowIndex, int colIndex) {
        System.out.println("generate mines");
        if (sweptCount > 0 || failed || generated) return;
        List<Cube> generatedCubes = new ArrayList<>();
        Random random = new Random(System.currentTimeMillis());
        int generateCount = 0;
        for (int row = 0; row < mode.getRowCount(); row++) {
            for (int col = 0; col < mode.getColumnCount(); col++) {
                //if()
                if (Math.abs(row - rowIndex) > 1 || Math.abs(col - colIndex) > 1) {//exclusive the first swept around cubes
                    generatedCubes.add(getCube(row, col));
                }
            }
        }
        while (generateCount++ < mode.getMineCount()) {
            int index = random.nextInt(generatedCubes.size());
            Cube mine = generatedCubes.remove(index);
            cubes[mine.getPosition().getRowIndex()][mine.getPosition().getColIndex()].setMine(true);
        }
        for (int i = 0; i < mode.getRowCount(); i++) {
            for (int j = 0; j < mode.getColumnCount(); j++) {
                cubes[i][j].setMinesCountAround(getMinesCountAround(i, j));
            }
        }
        generated = true;
    }

    void generateMines(CubePosition pos) {
        generateMines(pos.getRowIndex(), pos.getColIndex());
    }

    public int getMinesCountAround(CubePosition pos) {
        return getMinesCountAround(pos.getRowIndex(), pos.getColIndex());
    }

    public int getMinesCountAround(int rowIndex, int colIndex) {

        if (!isPositionValid(rowIndex, colIndex)) return -1;
        int count = 0;
        for (int row = rowIndex - 1; row <= rowIndex + 1; row++) {
            for (int col = colIndex - 1; col <= colIndex + 1; col++) {
                if (isPositionValid(row, col) && cubes[row][col].isMine()) count++;
            }
        }
        return count;
    }

    public boolean isSwept(int rowIndex, int colIndex) {
        if (!isPositionValid(rowIndex, colIndex) || !generated) return false;
        return cubes[rowIndex][colIndex].getState() == CubeState.EXCLUSIVE;
    }

    public void sweep(int rowIndex, int colIndex) {
        if (!isPositionValid(rowIndex, colIndex) || failed) return;
        if(gameStartTimeMills == -1)gameStartTimeMills = System.currentTimeMillis();
        if (!generated) {
            generateMines(rowIndex, colIndex);
        }
        Cube cube = getCube(rowIndex, colIndex);
        listener.sweeping(cube);
        switch (cube.getState()) {
            case INITIAL:
                cube.setState(cube.isMine() ? CubeState.EXPLODED : CubeState.EXCLUSIVE);
                listener.swept(cube);
                if (cube.isMine()) {
                    failed = true;
                    listener.failed(cube);
                } else {
                    sweptCount++;
                    if (sweptCount >= (mode.getColumnCount() * mode.getRowCount() - mode.getMineCount())) {
                        finished = true;
                        listener.finished();
                    }
                    if (cube.getMinesCountAround() == 0) {
                        sweepAround(rowIndex, colIndex);
                    }
                }
                break;
        }
    }

    void sweep(CubePosition pos) {
        sweep(pos.getRowIndex(), pos.getColIndex());
    }

    public void sweepAround(CubePosition pos) {
        sweepAround(pos.getRowIndex(), pos.getColIndex());
    }

    public void sweepAround(int row, int col) {
        System.out.println("sweepAround");
        if (!isPositionValid(row, col) || getCube(row, col).isMine()) return;
        Cube cube = getCube(row, col);
        for (int r = row - 1; r <= row + 1; r++) {
            for (int c = col - 1; c <= col + 1; c++) {
                if (isPositionValid(r, c)) sweep(r, c);
            }
        }
    }

    public void checkMarkedMineAndSweepAround(int row, int col) {
        if (!isPositionValid(row, col) || getCube(row, col).isMine() || getCube(row, col).getState() != CubeState.EXCLUSIVE)
            return;
        Cube cube = getCube(row, col);
        if (cube.getMinesCountAround() == getMarkedMineCountAround(cube.getPosition())) {
            for (int r = row - 1; r <= row + 1; r++) {
                for (int c = col - 1; c <= col + 1; c++) {
                    if (isPositionValid(r, c)) sweep(r, c);
                }
            }
        }
    }

    public void setState(int rowIndex, int colIndex, CubeState state) {
        if (!isPositionValid(rowIndex, colIndex)) return;
        cubes[rowIndex][colIndex].setState(state);
    }


    private boolean isPositionValid(CubePosition pos) {
        return isPositionValid(pos.getColIndex(), pos.getColIndex());
    }

    private boolean isPositionValid(int rowIndex, int colIndex) {
        return rowIndex < mode.getRowCount() && colIndex < mode.getColumnCount() && rowIndex >= 0 && colIndex >= 0;
    }

    private Cube getCube(CubePosition pos) {
        return getCube(pos.getRowIndex(), pos.getColIndex());
    }

    private Cube getCube(int rowIndex, int colIndex) {
        return isPositionValid(rowIndex, colIndex) ? cubes[rowIndex][colIndex] : null;
    }

    private int getMarkedMineCountAround(CubePosition position) {
        return getMarkedMineCountAround(position.getRowIndex(), position.getColIndex());
    }

    private int getMarkedMineCountAround(int rowIndex, int colIndex) {
        if (!isPositionValid(rowIndex, colIndex)) return -1;
        int count = 0;
        for (int row = rowIndex - 1; row <= rowIndex + 1; row++) {
            for (int col = colIndex - 1; col <= colIndex + 1; col++) {
                if (isPositionValid(row, col) && cubes[row][col].getState() == CubeState.MARK_MINE) count++;
            }
        }
        return count;
    }

    public void print() {
        System.out.println("print cubes:");
        for (int rowIndex = 0; rowIndex < cubes.length; rowIndex++) {
            for (int colIndex = 0; colIndex < cubes[rowIndex].length; colIndex++) {
                if (cubes[rowIndex][colIndex].isMine() && cubes[rowIndex][colIndex].getState() == CubeState.EXPLODED)
                    System.out.print(" # ");
                else if (cubes[rowIndex][colIndex].isMine())
                    System.out.print(" * ");
                else if (!cubes[rowIndex][colIndex].isMine())
                    System.out.print((cubes[rowIndex][colIndex].getState() == CubeState.EXCLUSIVE ? "|" : " ") + cubes[rowIndex][colIndex].getMinesCountAround() + (cubes[rowIndex][colIndex].getState() == CubeState.EXCLUSIVE ? "|" : " "));
            }
            System.out.println();
        }
    }
}
