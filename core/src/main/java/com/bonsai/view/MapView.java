package com.bonsai.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;
import com.bonsai.controller.GameController;
import com.bonsai.controller.factory.HumanViewFactory;
import com.bonsai.model.*;
import com.bonsai.view.animal.AnimalView;
import com.bonsai.view.human.HumanView;
import com.bonsai.view.jeep.JeepView;
import com.bonsai.view.terrain.GrassView;
import com.bonsai.view.terrain.TerrainView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapView {
    private GameController controller;
    private List<TerrainView> terrains;
    private List<AnimalView> animals;
    private List<HumanView> humans;
    private List<JeepView> jeeps;
    private List<ModelInstance> modelInstances = new ArrayList<>();
    private List<ModelInstance> animalInstances = new ArrayList<>();
    private List<ModelInstance> humanInstances = new ArrayList<>();
    private List<ModelInstance> jeepInstances = new ArrayList<>();
    private boolean mapChanged = false;
    private List<ModelInstance> wallInstances = new ArrayList<>();
    private Texture wallTextureDay;
    private Texture wallTextureSunset;
    private Texture wallTextureNight;
    private Time.TimeOfDay currentWallTime = null;
    private Map<ModelInstance, Animal> modelInstanceAnimalMap = new HashMap<>();
    private Map<ModelInstance, Jeep> modelInstanceJeepMap = new HashMap<>();
    private Map<ModelInstance, Ranger> modelInstanceRangerMap = new HashMap<>();
    private ViewUtils viewUtils;

    public MapView(GameController controller) {
        this.controller = controller;
        this.terrains = this.controller.getTerrains();
        setTerrains(this.terrains, 10f, this.controller.getMapSize());
        wallTextureDay = new Texture(Gdx.files.internal("landscape_day.png"));
        wallTextureSunset = new Texture(Gdx.files.internal("landscape_sunset.png"));
        wallTextureNight = new Texture(Gdx.files.internal("landscape_night.png"));
        setWalls(wallTextureDay);



    }

    public void setWalls(Texture wallTexture) {
        wallInstances.clear();

        float wallHeight = 100f;
        float wallThickness = 0.1f;
        float spacing = 10f;
        int mapSize = controller.getMapSize();
        float fullSize = mapSize * spacing;
        float offset = spacing / 2f;

        ModelBuilder builder = new ModelBuilder();
        Material material = new Material(TextureAttribute.createDiffuse(wallTexture));
        long usage = VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.TextureCoordinates;

        Model wallHorizontal = builder.createBox(fullSize, wallHeight, wallThickness, material, usage);
        Model wallHorizontalLonger = builder.createBox(fullSize+100.0f, wallHeight, wallThickness, material, usage);
        ModelInstance northWall = new ModelInstance(wallHorizontal);
        northWall.transform.setTranslation(fullSize / 2f - offset, wallHeight / 2f, -wallThickness / 2f - offset);
        Model wallVertical = builder.createBox(fullSize, wallHeight, wallThickness, material, usage);
        ModelInstance westWall = new ModelInstance(wallHorizontalLonger);
        westWall.transform.rotate(Vector3.Y, 90);
        westWall.transform.setTranslation(-wallThickness / 2f - offset, wallHeight / 2f, fullSize / 2f - offset);
        ModelInstance eastWall = new ModelInstance(wallHorizontalLonger);
        eastWall.transform.rotate(Vector3.Y, -90);
        eastWall.transform.setTranslation(fullSize + wallThickness / 2f - offset, wallHeight / 2f, fullSize / 2f - offset);

        wallInstances.add(northWall);
        wallInstances.add(westWall);
        wallInstances.add(eastWall);
    }

    public void setWallTimeOfDay(Time.TimeOfDay timeOfDay) {
        if (timeOfDay == currentWallTime) return;

        Texture newTexture;
        switch (timeOfDay) {
            case DAY:
                newTexture = wallTextureDay;
                break;
            case SUNSET:
                newTexture = wallTextureSunset;
                break;
            case NIGHT:
                newTexture = wallTextureNight;
                break;
            default:
                return;
        }

        // 壁マテリアルを更新
        Material newMat = new Material(TextureAttribute.createDiffuse(newTexture));
        for (ModelInstance wall : wallInstances) {
            wall.materials.get(0).clear();
            wall.materials.get(0).set(newMat);
        }

        currentWallTime = timeOfDay;
    }

    public void setTerrains(List<TerrainView> terrains, float terrainSpacing, int mapSize) {
        modelInstances.clear();


        for (int i = 0; i < terrains.size(); i++) {
            TerrainView terrain = terrains.get(i);

            // calculating coordinates by the given index
            float x = (i % mapSize) * terrainSpacing;
            float z = (i / mapSize) * terrainSpacing;

            for (ModelInstance modelInstance : terrain.getModelInstances(x, z)) {
                modelInstances.add(modelInstance);
            }
        }
    }

    public void setAnimal(List<AnimalView> animals){

        animalInstances.clear();

        List<Animal> tmp = controller.getAnimalsFromModel();
        for (int i = 0; i < tmp.size(); i++) {
            float x = tmp.get(i).getX();
            float z = tmp.get(i).getZ();

            AnimalView animal = animals.get(i);
            ModelInstance animalModelInstance = animal.getModelInstances(x, z).get(0);
            animalInstances.add(animalModelInstance);
            modelInstanceAnimalMap.put(animalModelInstance, tmp.get(i));
        }
    }

    public void setHuman(List<HumanView> humans) {
        humanInstances.clear();
        List<Human> tmp = controller.getHumanFromModel();

        for (int i = 0; i < tmp.size(); i++) {
            float x = tmp.get(i).getX();
            float z = tmp.get(i).getZ();

            HumanView human = humans.get(i);
            ModelInstance humanModelInstance = human.getModelInstances(x,z).get(0);
            humanInstances.add(humanModelInstance);

            if (tmp.get(i) instanceof Ranger) {
                modelInstanceRangerMap.put(humanModelInstance, (Ranger)tmp.get(i));
            }
        }
    }

    public void setJeep(List<JeepView> jeeps) {
        jeepInstances.clear();

        List<Jeep> tmp = controller.getJeepsFromModel();
        for (int i = 0; i < tmp.size(); i++) {
            float x = tmp.get(i).getX();
            float z = tmp.get(i).getZ();

            JeepView jeep = jeeps.get(i);

            for (ModelInstance modelInstance : jeep.getModelInstances(x, z)) {
                jeepInstances.add(modelInstance);
                modelInstanceJeepMap.put(modelInstance, tmp.get(i));
            }
        }
    }

    public void updateMap(int col, int row) {
        controller.updateTerrain(terrains, row, col);
        mapChanged = true;
    }

    public void updateJeep() {
        this.jeeps = this.controller.getJeeps();
    }

    public void updateAnimal() {
        this.animals = this.controller.getAnimals();
    }


    public void updateHuman(){ this.humans = this.controller.getHumans();}
    private void refreshMap() {
        if (mapChanged) {
            modelInstances.clear();

            setTerrains(this.terrains, 10f, this.controller.getMapSize());

            mapChanged = false;
        }
    }

    public void render(ModelBatch modelBatch, Environment environment, float delta) {
        refreshMap();



        for (ModelInstance modelInstance : modelInstances) {
            modelBatch.render(modelInstance, environment);
        }

        for(ModelInstance humanInstance : humanInstances){
            modelBatch.render(humanInstance, environment);
        }

        for (ModelInstance jeepInstance : jeepInstances) {
            modelBatch.render(jeepInstance, environment);
        }


        controller.updateAnimals(delta);
//        for(Animal animal : controller.getGameModel().getAnimalToRemove()){
//            controller.removeAnimal(animal);
//        }

        setAnimal(this.animals);

//        updateAnimalInstances(controller.getAnimalsFromModel());
        controller.updateHumans(delta);
        for(Human human : controller.getGameModel().getHumansToRemove()){
            controller.removeHuman(human);
        }
        setHuman(this.humans);

        controller.updateJeeps(delta);
        setJeep(this.jeeps);

        for (ModelInstance animalIns : animalInstances) {
            Animal animalModel = modelInstanceAnimalMap.get(animalIns);
            if (animalModel!= null) {
                if (shouldRenderAnimal(animalModel) && !animalModel.getMarkedForRemoval()) {
                    modelBatch.render(animalIns, environment);
                }
            }
        }

        for (ModelInstance wall : wallInstances) {
            modelBatch.render(wall, environment);
        }

        updateTourist();
    }

    public void updateAnimalInstances(List<Animal> animals) {
        animalInstances.clear();
        for (Animal animal : animals) {
            AnimalView view = controller.getAnimalView(animal);
            animalInstances.addAll(view.getModelInstances(animal.getX(), animal.getZ()));
        }
    }

    public boolean shouldRenderAnimal(Animal animal) {
        Time.TimeOfDay currentTimeOfDay = controller.getTime().getTimeOfDay();
        if (currentTimeOfDay != Time.TimeOfDay.NIGHT) {
            return true;
        }
        return isNearTouristOrRanger(animal) || animal.hasLocationChip();
    }

    private boolean isNearTouristOrRanger(Animal animal) {
        float visibilityRadius = 20f;
        Vector3 pos = animal.getPosition();

        List<Jeep> jeepModelList = controller.getJeepsFromModel();
        for (Jeep jeep : jeepModelList) {
            if (jeep.getPosition().dst(pos) < visibilityRadius) return true;
        }

        List<Ranger> rangerModelList = controller.getRangerFromModel();
        for (Ranger ranger : rangerModelList) {
            if (ranger.getPosition().dst(pos) < visibilityRadius) return true;
        }

        return false;
    }

    public void dispose() {
        for (ModelInstance modelInstance : modelInstances) {
            modelInstance.model.dispose();
        }

        modelInstances.clear();
    }



    public List<AnimalView> getAnimalView(){
        return animals;
    }

    private void updateTourist() {
        this.controller.updateTourist();
    }
}
