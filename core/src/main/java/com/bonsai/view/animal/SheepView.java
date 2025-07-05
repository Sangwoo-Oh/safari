package com.bonsai.view.animal;

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

import java.util.List;

public class SheepView extends AnimalView {

    public SheepView() {
        super();
        Texture texture = new Texture(Gdx.files.internal("sheep.png"));
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

        ModelInstance treeModelInstance = new ModelInstance(model);
        modelInstances.add(treeModelInstance);
        this.modelInstance = treeModelInstance;
    }


    @Override
    public List<ModelInstance> getModelInstances(float x, float z) {
        ModelInstance treeModelInstance = modelInstances.get(0);

        treeModelInstance.transform.idt();

        treeModelInstance.transform.setToTranslation(x, 5f, z);
        treeModelInstance.transform.rotate(Vector3.X, 180f);
        treeModelInstance.transform.rotate(Vector3.Z, -90f);

        return modelInstances;
    }
}
