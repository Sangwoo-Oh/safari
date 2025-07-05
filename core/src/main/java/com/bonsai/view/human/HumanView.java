package com.bonsai.view.human;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.bonsai.model.Human;

import java.util.ArrayList;
import java.util.List;

public abstract class HumanView {

    protected List<ModelInstance> modelInstances;
    protected ModelInstance modelInstance;
    protected float size = 10f;

    HumanView(){
        modelInstances = new ArrayList<>();
    }

    public abstract List<ModelInstance> getModelInstances(float x, float z);
}
