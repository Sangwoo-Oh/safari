package com.bonsai.view;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Plane;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.bonsai.controller.GameController;

public class TileSelector {
    private final Camera camera;
    private final Environment environment;
    private final ModelBatch modelBatch;
    private final ModelInstance highlightInstance;
    private final Model highlightModel;
    private final Vector3 intersection = new Vector3();
    private final Plane groundPlane = new Plane(Vector3.Y, 0);
    private final float tileSize = 10f;

    private ViewUtils viewUtils;
    private GameController controller;

    private boolean enabled = false;

    public TileSelector(Camera camera, Environment environment, GameController controller) {
        this.camera = camera;
        this.environment = environment;
        this.modelBatch = new ModelBatch();
        this.controller = controller;
        this.viewUtils = new ViewUtils(camera, this.controller.getMapSize());

        ModelBuilder modelBuilder = new ModelBuilder();
        highlightModel = modelBuilder.createBox(
            tileSize, 0.2f, tileSize,
            new Material(ColorAttribute.createDiffuse(new Color(1, 1, 0, 0.4f))),
            VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal
        );
        highlightInstance = new ModelInstance(highlightModel);
        highlightInstance.transform.setToTranslation(0, -999, 0);
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void updateHover() {
        if (!enabled) return;
        int mouseX = Gdx.input.getX();
        int mouseY = Gdx.input.getY();

        Ray ray = camera.getPickRay(mouseX, mouseY);

        if (Intersector.intersectRayPlane(ray, groundPlane, intersection)) {
            int[] tileXZ = viewUtils.getTileCoordinate(mouseX, mouseY);
            int tileX = tileXZ[0];
            int tileZ = tileXZ[1];
            int mapSize = controller.getMapSize();
            if (tileX < 0 || tileX >= mapSize || tileZ < 0 || tileZ >= mapSize) {
                highlightInstance.transform.setToTranslation(0, -999, 0);
            } else {
                highlightInstance.transform.setToTranslation(tileX * tileSize, 0.1f, tileZ * tileSize);
            }
        }
    }

    public void render() {
        if (!enabled) return;
        modelBatch.begin(camera);
        modelBatch.render(highlightInstance, environment);
        modelBatch.end();
    }

    public void dispose() {
        modelBatch.dispose();
        highlightModel.dispose();
    }
}
