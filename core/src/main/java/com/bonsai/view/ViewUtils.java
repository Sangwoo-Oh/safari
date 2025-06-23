package com.bonsai.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;
import com.bonsai.model.Animal;
import com.bonsai.view.animal.AnimalView;

import java.util.List;

public class ViewUtils {
    private Camera camera;
    private int mapSize;
    public ViewUtils (Camera camera, int mapSize) {
        this.camera = camera;
        this.mapSize = mapSize;
    }
    public int tileX = -1;
    public int tileZ = -1;

    public int[] getTileCoordinate(int screenX, int screenY) {
        Ray ray = camera.getPickRay(screenX, screenY);
        float t = -ray.origin.y / ray.direction.y;
        if (t > 0) {
            float worldX = ray.origin.x + t * ray.direction.x;
            float worldZ = ray.origin.z + t * ray.direction.z;
//            System.out.println("Clicked a: (" + (worldX / 10f) + ", " + (worldZ / 10f) + ")");
            // 3) タイル座標に変換(例: 10ユニットごとに区切られたタイル)
            tileX = (int) Math.round(worldX / 10f);
            tileZ = (int) Math.round(worldZ / 10f);

            return new int[]{tileX, tileZ};
        } else {
            return new int[]{-1,-1};
        }
    }

    public void renderDebugBoxes(Camera camera, List<AnimalView> animalViews, ShapeRenderer shapeRenderer) {
        if (animalViews == null || animalViews.isEmpty()) return;

        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line); // 線だけ描く

        for (AnimalView view : animalViews) {
            ModelInstance instance = view.getModelInstanceTMP();
            if (instance == null) continue;

            BoundingBox bounds = new BoundingBox();


            instance.calculateBoundingBox(bounds);
            bounds.mul(instance.transform); // ← transform反映！
            bounds.ext(new Vector3(0f, 0f, 0.5f)); // Z方向にだけ厚みを追加！

            Vector3 min = bounds.min;
            Vector3 max = bounds.max;

            Vector3 center = bounds.getCenter(new Vector3());
//            System.out.println("Box center: " + center);
//            System.out.println("Box size: " + bounds.getDimensions(new Vector3()));
//

            // 当たり判定の枠描画（赤）
            shapeRenderer.setColor(Color.RED);
            shapeRenderer.box(
                min.x,
                min.y,
                min.z,
                max.x - min.x,
                max.y - min.y,
                max.z - min.z
            );
        }

        shapeRenderer.end();

    }






}
