package com.bonsai.view.terrain;

import com.badlogic.gdx.graphics.g3d.ModelInstance;

import java.util.ArrayList;
import java.util.List;

public abstract class TerrainView {
    protected List<ModelInstance> modelInstances;
    protected float size = 10f;
    TerrainView() {
        modelInstances = new ArrayList<>();
    }

    public abstract List<ModelInstance> getModelInstances(float x, float z);
}
