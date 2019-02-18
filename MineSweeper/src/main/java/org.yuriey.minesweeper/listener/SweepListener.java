package org.yuriey.minesweeper.listener;

import org.yuriey.minesweeper.model.Cube;
import org.yuriey.minesweeper.model.CubeState;

public interface SweepListener {
    void sweeping(Cube cube);
    void swept(Cube cube);
    void failed(Cube explodedCube);
    void finished();
    void onStateChange(Cube cube, CubeState oldState, CubeState newState);
}
