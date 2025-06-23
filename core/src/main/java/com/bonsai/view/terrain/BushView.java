package com.bonsai.view.terrain;

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

public class BushView extends TerrainView {
    public BushView() {
        super();
        Texture texture = new Texture(Gdx.files.internal("bush_test.png"));
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

        Texture grassTexture = new Texture(Gdx.files.internal("Grass_top_view_test.png"));
        Material grassMaterial = new Material(TextureAttribute.createDiffuse(grassTexture));

        Model grassModel =  modelBuilder.createRect(
            -size / 2, 0, -size / 2,
            -size / 2, 0, size / 2,
            size / 2, 0, size / 2,
            size / 2, 0, -size / 2,
            0, 1, 0,
            grassMaterial,
            VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.TextureCoordinates
        );
        modelInstances.add(new ModelInstance(grassModel));
    }

    @Override
    public List<ModelInstance> getModelInstances(float x, float z) {
        ModelInstance treeModelInstance = modelInstances.get(0);
        treeModelInstance.transform.setToTranslation(x, 5f, z);
        treeModelInstance.transform.rotate(Vector3.X, 180f);
        treeModelInstance.transform.rotate(Vector3.Z, -90f);

        ModelInstance grassModelInstance = modelInstances.get(1);
        grassModelInstance.transform.setToTranslation(x,0,z);
        return modelInstances;
    }
}
