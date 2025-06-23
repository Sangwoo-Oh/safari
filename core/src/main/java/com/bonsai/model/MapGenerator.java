package com.bonsai.model;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.math.MathUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class MapGenerator {
    private static final int MAP_SIZE = 20;
    private static final int TREE_DIVISOR = 64;
    private static final int POND_COUNT = 4;
    private static final boolean BUSH_SAME_AS_TREE = true;
    private int entrancePosX, entrancePosY, exitPosX, exitPosY;
    private Terrain[][] template1 = new Terrain[MAP_SIZE][MAP_SIZE];
    private GameModel gameModel;

    public MapGenerator(GameModel model) {
        this.gameModel = model;

        entrancePosX = model.getEntrancePosX();
        entrancePosY = model.getEntrancePosY();
        exitPosX = gameModel.getExitPosX();
        exitPosY = gameModel.getExitPosY();

//        System.out.println("entrance " + entrancePosX + " : " + entrancePosY);
//        System.out.println("entrance " + exitPosX + " : " + exitPosY);

        int choice = MathUtils.random(1, 3);
        switch (choice) {
            case 1:
                template1 = buildTemplate(template1);
                break;
            case 2:
                template1 = buildTemplate(template1);
                break;
            default:
                template1 = buildTemplate(template1);
                break;
        }
    }


    private Terrain[][] buildTemplate(Terrain[][] m) {
        for (int i = 0; i < MAP_SIZE; i++) {
            for (int j = 0; j < MAP_SIZE; j++) {
                m[i][j] = new Grass(i, j);
            }
        }

        int totalCells = MAP_SIZE * MAP_SIZE;
        int treeCount  = totalCells / TREE_DIVISOR;
        int bushCount  = BUSH_SAME_AS_TREE ? treeCount : treeCount / TREE_DIVISOR;

        List<int[]> cells = new ArrayList<>(totalCells);
        for (int i = 0; i < MAP_SIZE; i++) {
            for (int j = 0; j < MAP_SIZE; j++) {
                cells.add(new int[]{i, j});
            }
        }
        Collections.shuffle(cells, new Random());

        for (int k = 0; k < treeCount && k < cells.size(); k++) {
            int[] cell = cells.get(k);
            m[cell[0]][cell[1]] = new Tree(cell[0], cell[1]);
        }
        for (int k = treeCount; k < treeCount + bushCount && k < cells.size(); k++) {
            int[] cell = cells.get(k);
            m[cell[0]][cell[1]] = new Bush(cell[0], cell[1]);
        }

        for (int k = 0; k < POND_COUNT; k++) {
            int px = MathUtils.random(1, MAP_SIZE - 2);
            int py = MathUtils.random(1, MAP_SIZE - 2);
            addPondToMap(m, px, py);
        }
        Random random = new Random();
        int r = new Random().nextInt(2);
        if (r == 0) {
            generateRoad1(m);
        } else {
            generateRoad2(m);
        }
        return m;
    }

    private void generateRoad1(Terrain[][] m){

        gameModel.setExitPosX(19);
        gameModel.setExitPosY(14);
        exitPosX = gameModel.getExitPosX();
        exitPosY = gameModel.getExitPosY();

        for(int i = 0; i < MAP_SIZE; i++){
            for (int j = 0; j < MAP_SIZE; j++) {

                if(i == 5 && j < 6){
                    m[i][j] = new Road(i, j);
                }

                if(j==6 && i >= 5 && i < 10){
                    m[i][j] = new Road(i, j);
                }

                if( j >= 6 && j < 17 && i == 10){
                    m[i][j] = new Road(i, j);
                }

                if(i > 10 && i <= 14 && j == 16){
                    m[i][j] = new Road(i, j);
                }

                if(i == 14 && j > 16 && j < MAP_SIZE - 1){
                    m[i][j] = new Road(i, j);
                }


            }
        }


        m[entrancePosY][entrancePosX] = new Entrance(entrancePosX, entrancePosY);
        m[exitPosY][exitPosX]         = new Exit(exitPosX, exitPosY);

    }
    private void generateRoad2(Terrain[][] m) {

        for (int i = 0; i < MAP_SIZE; i++) {
            for (int j = 0; j < MAP_SIZE; j++) {

                if (j == 5 && i < 6) {
                    m[i][j] = new Road(i, j);
                }

                if (i == 6 && j >= 5 && j < 10) {
                    m[i][j] = new Road(i, j);
                }

                if (i >= 6 && i < 17 && j == 10) {
                    m[i][j] = new Road(i, j);
                }

                if (j > 10 && j <= 14 && i == 16) {
                    m[i][j] = new Road(i, j);
                }


                if (j == 14 && i > 16 && i < MAP_SIZE - 1) {
                    m[i][j] = new Road(i, j);
                }
            }
        }

        gameModel.setExitPosX(14);
        gameModel.setExitPosY(19);
        exitPosX = gameModel.getExitPosX();
        exitPosY = gameModel.getExitPosY();

        gameModel.setEntrancePosX(5);
        gameModel.setEntrancePosY(0);
        entrancePosY = gameModel.getEntrancePosY();
        entrancePosX = gameModel.getEntrancePosX();

        m[entrancePosY][entrancePosX] = new Entrance(entrancePosX, entrancePosY);
        m[exitPosY][exitPosX]         = new Exit(exitPosX, exitPosY);
    }


    public void addPondToMap(Terrain[][] m, int x, int y) {
        m[x][y]       = new Pond(x, y);
        m[x + 1][y]   = new Pond(x + 1, y);
        m[x][y + 1]   = new Pond(x, y + 1);
        m[x + 1][y + 1] = new Pond(x + 1, y + 1);
    }


    public Terrain[][] getTemplate() {
        return template1;
    }
}
