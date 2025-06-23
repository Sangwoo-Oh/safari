package com.bonsai.view.animal;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import java.util.ArrayList;
import java.util.List;

public abstract class AnimalView {
    protected List<ModelInstance> modelInstances;
    protected ModelInstance modelInstance;
    protected float size = 10f;
    AnimalView() {
        modelInstances = new ArrayList<>();
    }
    public List<ModelInstance> getModelInstancesTest(){
        return this.modelInstances;
    };

    public ModelInstance getModelInstanceTMP(){
        return this.modelInstance;
    }
    public abstract List<ModelInstance> getModelInstances(float x, float z);
    public abstract List<ModelInstance> updateModelInstances(float x, float z);

    public List<ModelInstance> updateModelInstancesTest(float x, float z) {
        modelInstance.transform.setToTranslation(x, 0f, z);
        return modelInstances;
    }


}
