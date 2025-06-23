package com.bonsai.model;

class GameThresholds {
    int minHerbivores;
    int minCarnivores;
    int minTourists;
    int minCapital;

    public GameThresholds(int h, int c, int t, int cap) {
        this.minHerbivores = h;
        this.minCarnivores = c;
        this.minTourists = t;
        this.minCapital = cap;
    }
}
