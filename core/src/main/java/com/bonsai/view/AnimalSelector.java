package com.bonsai.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Plane;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;
import com.bonsai.controller.GameController;
import com.bonsai.model.Animal;
import com.bonsai.view.animal.AnimalView;

import java.util.List;

public class AnimalSelector {
    private GameController controller;
    private final ModelInstance highlightInstance;
    private final Model highlightModel;
    private final float tileSize = 10f;


    public AnimalSelector(Camera camera, Environment environment, GameController controller) {
        this.controller = controller;

        ModelBuilder modelBuilder = new ModelBuilder();
        highlightModel = modelBuilder.createBox(
            tileSize, 0.2f, tileSize,
            new Material(ColorAttribute.createDiffuse(new Color(1, 1, 0, 0.4f))),
            VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal
        );
        highlightInstance = new ModelInstance(highlightModel);
        highlightInstance.transform.setToTranslation(0, -999, 0);
    }

    public Animal selectClosestAnimal(Camera camera, List<Animal> animals) {
        Ray ray = camera.getPickRay(Gdx.input.getX(), Gdx.input.getY());

        // まず地面との交点を計算（XZ平面）
        Plane ground = new Plane(Vector3.Y, 0);
        Vector3 clickPoint = new Vector3();
        if (!Intersector.intersectRayPlane(ray, ground, clickPoint)) {
            return null; // 地面に当たらなかったら何も選択しない
        }

        Animal closestAnimal = null;
        float closestDistance = Float.MAX_VALUE;

        for (Animal animal : animals) {
            Vector3 animalPos = animal.getPosition();
            float distance2D = clickPoint.dst2(animalPos); // dst2 = 距離の2乗（高速）

            if (distance2D < closestDistance) {
                closestDistance = distance2D;
                closestAnimal = animal;
            }
        }

        // 任意：近すぎないと選ばない（10ユニット以内とか）
        if (closestAnimal != null && Math.sqrt(closestDistance) < 10f) {
            System.out.println("Selected animal at: " + closestAnimal.getPosition());
            return closestAnimal;
        }

        return null;
    }



}
