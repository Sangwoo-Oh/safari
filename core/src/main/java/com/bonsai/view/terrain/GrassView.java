package com.bonsai.view.terrain;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;

import java.util.List;

public class GrassView extends TerrainView {
    public GrassView() {
        super();
        Texture texture = new Texture(Gdx.files.internal("Grass_top_view_test.png"));
        Material material = new Material(TextureAttribute.createDiffuse(texture));
        ModelBuilder modelBuilder = new ModelBuilder();

        Model model =  modelBuilder.createRect(
            -size / 2, 0, -size / 2,
            -size / 2, 0, size / 2,
            size / 2, 0, size / 2,
            size / 2, 0, -size / 2,
            0, 1, 0,
            material,
            VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.TextureCoordinates
        );

        this.modelInstances.add(new ModelInstance(model));
    }

    public List<ModelInstance> getModelInstances(float x, float z) {
        for (ModelInstance modelInstance: modelInstances) {
            modelInstance.transform.setToTranslation(x, 0, z);
        }
        return modelInstances;
    }
}
