package com.bonsai.model;

import com.badlogic.gdx.math.Vector3;

import com.badlogic.gdx.math.Vector3;

public abstract class Terrain {
    private int x;
    private int z;
    private boolean isEntrance = false;
    private boolean isExit = false;

    public Terrain(int x, int z) {
        this.x = x;
        this.z = z;
    }

    public int getX() {
        return x;
    }

    public int getZ() {
        return z;
    }

    public Vector3 getPosition() {
        return new Vector3(x, 0, z);  // yは地面なので 0、高さがある場合は調整
    }

    public boolean getIsEntrance() {
        return isEntrance;
    }

    public boolean isEntrance() {
        return isEntrance;
    }

    public void setEntrance(boolean entrance) {
        isEntrance = entrance;
    }

    public boolean isExit() {
        return isExit;
    }

    public void setExit(boolean exit) {
        isExit = exit;
    }
}

