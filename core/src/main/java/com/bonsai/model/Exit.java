package com.bonsai.model;

public class Exit extends Terrain {
    public Exit(int x, int y) {
        super(x, y);
        super.setExit(true);
    }
}

