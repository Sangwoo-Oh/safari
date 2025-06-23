package com.bonsai.model;

public class Entrance extends Terrain {
    public Entrance(int x, int y) {
        super(x, y);
        super.setEntrance(true);
    }
}
