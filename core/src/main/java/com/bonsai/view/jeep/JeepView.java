package com.bonsai.view.jeep;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;
import com.bonsai.view.animal.AnimalView;

import java.util.ArrayList;
import java.util.List;

public class JeepView {
    protected List<ModelInstance> modelInstances;
    protected ModelInstance modelInstance;
    protected float size = 10f;

    public JeepView() {
        modelInstances = new ArrayList<>();
        Texture texture = new Texture(Gdx.files.internal("jeep.png"));
        Material material = new Material(
            TextureAttribute.createDiffuse(texture),
            new BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)
        );
        ModelBuilder modelBuilder = new ModelBuilder();

        Model model = modelBuilder.createRect(
            -size / 2, -size / 2, 0,
            -size / 2, size / 2, 0,
            size / 2, size / 2, 0,
            size / 2, -size / 2, 0,
            0, 0, 1,
            material,
            VertexAttributes.Usage.Position |
                VertexAttributes.Usage.Normal |
                VertexAttributes.Usage.TextureCoordinates
        );

        modelInstance = new ModelInstance(model);
        modelInstances.add(modelInstance);
    }


    public List<ModelInstance> getModelInstances(float x, float z) {
        ModelInstance treeModelInstance = modelInstances.get(0);
        treeModelInstance.transform.setToTranslation(x, 5f, z);
        treeModelInstance.transform.rotate(Vector3.X, 180f);
        treeModelInstance.transform.rotate(Vector3.Z, -90f);
        return modelInstances;
    }
}
