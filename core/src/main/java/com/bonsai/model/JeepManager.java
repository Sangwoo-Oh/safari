package com.bonsai.model;

import com.bonsai.model.interfaces.MapChangeObserver;

import java.util.ArrayList;
import java.util.List;

public class JeepManager implements MapChangeObserver {
    private GameModel gameModel;
    private char[][] graph;
    private List<Jeep> jeeps = new ArrayList<>();

    public JeepManager(GameModel model) {
        this.gameModel = model;
        rebuildGraph();
    }

    public Jeep createJeep() {
        Jeep jeep = new Jeep(this);
        if (!isNonTourists()) {
            gameModel.assignTouristsToJeep(jeep);
        }
        jeeps.add(jeep);
        return jeep;
    }

    @Override
    public void onMapChanged() {
        rebuildGraph();

        float baseDelay = 0f;
        for (Jeep jeep : jeeps) {
            jeep.refreshPathWithDelay(baseDelay);
            baseDelay += 1.0f;
        }
    }

    public void rebuildGraph() {
        Terrain[][] map = gameModel.getMap();
        int size = gameModel.getMAP_SIZE();
        graph = new char[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                Terrain t = map[i][j];
                if (t instanceof Road) graph[i][j] = 'R';
                else if (t instanceof Entrance) graph[i][j] = 'S';
                else if (t instanceof Exit) graph[i][j] = 'G';
                else graph[i][j] = '#';
            }
        }
    }

    public char[][] getGraph() {
        return graph;
    }

    public int getEntranceX() {
        return gameModel.getEntrancePosX();
    }

    public int getEntranceY() {
        return gameModel.getEntrancePosY();
    }

    public Terrain[][] getMap() {
        return gameModel.getMap();
    }

    public Time getTime() {
        return gameModel.getTime();
    }

    public void reassignTouristsToJeep(Jeep jeep) {
        gameModel.assignTouristsToJeep(jeep);
    }

    public boolean isNonTourists() {
        return gameModel.getTourist().isEmpty();
    }

    public GameModel getGameModel() {
        return gameModel;
    }
}
