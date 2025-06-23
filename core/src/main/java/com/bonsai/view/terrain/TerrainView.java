package com.bonsai.view.terrain;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;

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
