package com.bonsai.model;

import com.badlogic.gdx.math.Vector3;

import java.util.ArrayList;
import java.util.List;

public abstract class Herbivorous extends Animal {
    protected List<Terrain> visitedTerrains = new ArrayList<>();
    private Vector3 foodTargetPosition = null;
    private final float HUNGER_VALUE = 150;
    protected float hunger = HUNGER_VALUE;
    private float arrivalTime = 3;
    private boolean isEating = false;


    public Herbivorous(float x, float y, float z, Time time) {
        super(x, y, z, time);
    }

    @Override
    public void update(float delta, Time time, List<Animal> allAnimals) {

        if (dead) {
            this.markedForRemoval = true;
        }
        if (isEating) {
            this.overLifespan(time);
            arrivalTime -= delta;  // 減算してカウントダウン
            if (arrivalTime <= 0) {
                // 待機が終了したら次の目的地に向かう
                arrivalTime = 3;
                isEating = false;
                fillHunger(time);
            }
        } else if (this.hungry) {
            foodTargetPosition = null;
            this.findFood();
            this.moveToPlant(delta, time);
            removeChangePlants();
            visitTerrain();
            getHungry(time);
            if (foodTargetPosition == null) {
                this.herbivorousMoveSet(delta, time, allAnimals);
            }
        } else {
            this.herbivorousMoveSet(delta, time, allAnimals);
        }
    }

    private void herbivorousMoveSet(float delta, Time time, List<Animal> allAnimals) {
        super.update(delta, time, allAnimals);
        removeChangePlants();
        visitTerrain();
        this.getHungry(time);
    }


    //    store the terrain which is tree or grass
//    protected void visitTerrain() {
//        Terrain currentTerrain = gameController.getCurrentTerrain(this);
//        if ((currentTerrain instanceof Tree || currentTerrain instanceof Bush) && !visitedTerrains.contains(currentTerrain)) {
//            visitedTerrains.add(currentTerrain);

    /// /            System.out.println("added");
//        }
//    }
    protected void visitTerrain() {
        Terrain currentTerrain = gameController.getCurrentTerrain(this);
        if ((currentTerrain instanceof Tree || currentTerrain instanceof Bush) && !visitedTerrains.contains(currentTerrain)) {
            visitedTerrains.add(currentTerrain);
            if (herd != null) {
                herd.addSharedFoodSource(currentTerrain);
            }
        }
    }

//    public void findFood() {
//        Terrain closestFoodPlant = findClosestFoodTerrain();
//
//        if (closestFoodPlant != null) {
//            foodTargetPosition = new Vector3(closestFoodPlant.getX(), 0, closestFoodPlant.getZ()); // ★追加
//        }
//    }

    //    @Override
    public void findFood() {
        Terrain closestFood = null;
        float minDistance = Float.MAX_VALUE;
        Vector3 currentPosition = getPosition();

        // 自身の visitedTerrains のみから探索
        for (Terrain terrain : visitedTerrains) {
            float distance = currentPosition.dst(new Vector3(terrain.getX(), 0, terrain.getZ()));
            if (distance < minDistance) {
                minDistance = distance;
                closestFood = terrain;
            }
        }

        if (closestFood != null) {
            foodTargetPosition = new Vector3(closestFood.getX(), 0, closestFood.getZ());
        }
    }


    private Terrain findClosestFoodTerrain() {
        Terrain closestFoodTerrain = null;
        float minDistance = Float.MAX_VALUE;

        Vector3 currentPosition = getPosition();

        for (Terrain terrain : visitedTerrains) {
            float distance = currentPosition.dst(new Vector3(terrain.getX() * 10, 0, terrain.getZ() * 10));
            if (distance < minDistance) {
                minDistance = distance;
                closestFoodTerrain = terrain;
            }
        }

        return closestFoodTerrain;
    }


    private void moveToPlant(float delta, Time time) {
        // 最も近い食物を含む地形（草や木）を見つける
        Terrain closestTerrain = findClosestFoodTerrain();

        if (closestTerrain != null) {
            // 現在の位置を取得
            Vector3 pos = getPosition();
            // 最寄のターゲット地形の位置を取得
            Vector3 targetPosition = new Vector3(closestTerrain.getX() * 10, 0, closestTerrain.getZ() * 10);

            // 新しい次の位置を設定
            this.next_position = targetPosition;

            // 目標地形に向かって移動する方向ベクトルを計算
            Vector3 direction = new Vector3(next_position).sub(pos);
            if (direction.len() != 0) {
                direction.nor();  // 単位ベクトルに変換
            }

            // 現在位置を更新
            pos.mulAdd(direction, speed * delta);

            // 新しい位置を設定
            setPosition(pos);

            // 目標地形に到達したかチェック
            if (pos.dst(next_position) < 1f) {
                this.isEating = true;
            }
        }
    }

    private void removeChangePlants() {
        List<Terrain> toRemove = new ArrayList<>();
        for (Terrain terrain : visitedTerrains) {
            Terrain currentTerrain = gameController.getTerrain((int) terrain.getPosition().x, (int) terrain.getPosition().z);
            if (!(currentTerrain instanceof Tree || currentTerrain instanceof Bush)) {
                toRemove.add(terrain);
            }
        }
        visitedTerrains.removeAll(toRemove);
    }

    private void fillHunger(Time time) {
        this.hungerTime = time.getTotalElapsedGameHours() / 24;
        this.hungry = false;
    }

    @Override
    public void getHungry(Time time) {
        if (age / lifespan <= 0.3) {
            gettingHungry(time, 1.5f);
        } else if (0.3 < age / lifespan && age / lifespan <= 0.6) {
            gettingHungry(time, 2f);
        } else if (age / lifespan > 0.6) {
            gettingHungry(time, 3.5f);
        }
    }

    private void gettingHungry(Time time, float x) {
        float elapsedTime = time.getTotalElapsedGameHours() / 24f - this.hungerTime;
        if (elapsedTime >= this.hunger) {
            this.die();
        } else if (elapsedTime >= (float) this.hunger / x) {
            this.hungry = true;
        }
    }

}

