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

    private final Camera camera;
    private final Environment environment;
    private final ModelBatch modelBatch;
    private GameController controller;
    private ViewUtils viewUtils;
    private final ModelInstance highlightInstance;
    private final Model highlightModel;
    private final Vector3 intersection = new Vector3();
    private final Plane groundPlane = new Plane(Vector3.Y, 0);
    private final float tileSize = 10f;


    public AnimalSelector(Camera camera, Environment environment, GameController controller) {
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


    public Animal selectAnimalUnderCursor(Camera camera, List<Animal> animals) {
        Ray ray = camera.getPickRay(Gdx.input.getX(), Gdx.input.getY());

        Animal closestAnimal = null;
        float closestDistance = Float.MAX_VALUE;

//        for (Animal animal : animals) {
//            AnimalView view = controller.getAnimalView(animal);
//            ModelInstance instance = view.getModelInstanceTMP(); // もしくは modelInstances.get(0)
//
//            BoundingBox bounds = new BoundingBox();
//            instance.calculateBoundingBox(bounds);
//            bounds.mul(instance.transform); // transform に従って位置補正
//
//            Vector3 intersection = new Vector3();
//            if (Intersector.intersectRayBounds(ray, bounds, intersection)) {
//                float dist = ray.origin.dst2(intersection); // カメラからの距離を計算（近い順に選びたい）
//                if (dist < closestDistance) {
//                    closestDistance = dist;
//                    closestAnimal = animal;
//                }
//            }
//        }
        for (Animal animal : controller.getGameModel().getAnimals()) {
            AnimalView view = controller.getAnimalView(animal);
            ModelInstance instance = view.getModelInstanceTMP(); // モデル取得
            BoundingBox bounds = new BoundingBox();
            bounds.ext(0f, 0f, 0.5f);
            instance.calculateBoundingBox(bounds);
            bounds.mul(instance.transform); // 🔥←重要！

            Vector3 intersection = new Vector3();
            System.out.println("Bounds: " + bounds);


            if (Intersector.intersectRayBounds(ray, bounds, intersection)) {
                System.out.println("HIT Animal at " + animal.getPosition());
                return animal;
            }
        }
        if(closestAnimal == null){
            System.out.println("animals is null");
        }
        return closestAnimal; // null なら未選択


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
