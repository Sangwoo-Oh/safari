package com.bonsai.controller;


import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.bonsai.controller.factory.AnimalViewFactory;
import com.bonsai.controller.factory.HumanViewFactory;
import com.bonsai.controller.factory.JeepViewFactory;
import com.bonsai.controller.factory.TerrainViewFactory;
import com.bonsai.model.*;
import com.bonsai.view.animal.*;
import com.bonsai.view.human.HumanView;
import com.bonsai.view.human.PoacherView;
import com.bonsai.view.human.RangerView;
import com.bonsai.view.jeep.JeepView;
import com.bonsai.view.terrain.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class GameController {
    private static GameController instance;
    private TerrainViewFactory terrainFactory;
    private AnimalViewFactory animalFactory;
    private JeepViewFactory jeepFactory;
    private HumanViewFactory humanFactory;
    private GameModel model;
    float size = 10f;
    private int MAP_SIZE;
    private boolean needUpdate;
    private Random r;


    private GameController(GameModel model) {
        this.model = model;
        this.terrainFactory = new TerrainViewFactory();
        this.animalFactory = new AnimalViewFactory();
        this.jeepFactory = new JeepViewFactory();
        this.humanFactory = new HumanViewFactory();
        registerTerrains();
        registerAnimals();
        registerJeeps();
        registerHuman();
        MAP_SIZE = this.model.getMAP_SIZE();
        r = new Random();
    }

    private void registerTerrains() {
        terrainFactory.registerTerrain(Grass.class, () -> new GrassView());
        terrainFactory.registerTerrain(Road.class, () -> new RoadView());
        terrainFactory.registerTerrain(Pond.class, () -> new PondView());
        terrainFactory.registerTerrain(Tree.class, () -> new TreeView());
        terrainFactory.registerTerrain(Bush.class, () -> new BushView());
        terrainFactory.registerTerrain(Entrance.class, () -> new EntranceView());
        terrainFactory.registerTerrain(Exit.class, () -> new ExitView());
    }

    private void registerAnimals() {
        animalFactory.registerAnimal(Sheep.class, () -> new SheepView());
        animalFactory.registerAnimal(Horse.class, () -> new HorseView());
        animalFactory.registerAnimal(Lion.class, () -> new LionView());
        animalFactory.registerAnimal(Cheetah.class, () -> new CheetahView());
    }

    private void registerHuman() {
        humanFactory.registerHuman(Ranger.class, () -> new RangerView());
        humanFactory.registerHuman(Poacher.class, () -> new PoacherView());
    }

    private void registerJeeps() {
        jeepFactory.registerJeep(Jeep.class, () -> new JeepView());
    }

    public List<TerrainView> getTerrains() {
        List<TerrainView> terrains = new ArrayList<>();
        Terrain[][] map = model.getMap();
        for (Terrain[] row : map) {
            for (Terrain terrain : row) {
                if (terrain != null) {
                    terrains.add(terrainFactory.createTerrain(terrain));
                }
            }
        }
        return terrains;
    }

    public void updateTerrain(List<TerrainView> terrains, int row, int col) {
        Terrain[][] map = model.getMap();
        Terrain terrain = map[row][col];
        terrains.set(row * MAP_SIZE + col, terrainFactory.createTerrain(terrain));
    }

    public Terrain getTerrain(int x, int y) {
        return this.model.getTerrain(x, y);

    }

    public List<AnimalView> getAnimals() {
        List<AnimalView> animalsView = new ArrayList<>();
        List<Animal> animals = model.getAnimals();
        for (Animal animal : animals) {
            animalsView.add(animalFactory.createAnimal(animal));
        }

        return animalsView;
    }

    public List<HumanView> getHumans() {
        List<HumanView> humansView = new ArrayList<>();
        List<Human> humans = model.getHumans();
        for (Human human : humans) {
            humansView.add(humanFactory.createHuman(human));
        }
        return humansView;
    }

    public List<JeepView> getJeeps() {
        List<JeepView> jeepsView = new ArrayList<>();
        List<Jeep> jeeps = model.getJeeps();
        for (Jeep jeep : jeeps) {
            jeepsView.add(jeepFactory.createJeep(jeep));
        }
        return jeepsView;
    }

    public void updateTerrain(int x, int z, Terrain terrain) {
        if (x < 0 || x >= MAP_SIZE || z < 0 || z >= MAP_SIZE) return;
        this.model.setTerrain(x, z, terrain);
    }

    //    public void updateAnimals(float delta) {
//        List<Animal> animals = model.getAnimals();
//        for (Animal al : animals) {
//            al.update(delta);
//        }
//
//    }
    public void updateAnimals(float delta) {
        List<Animal> animals = model.getAnimals();
        Iterator<Animal> iterator = animals.iterator();  // 動物リストのイテレータを取得
        while (iterator.hasNext()) {
            Animal animal = iterator.next();
            animal.update(delta, model.getTime(), model.getAnimals());  // 動物の状態を更新

            // 動物が削除対象かどうかを確認し、削除
            if (animal.getMarkedForRemoval()) {
                iterator.remove();  // 動物をリストから削除
            }
        }
    }


    //    public void updateHumans(float delta){
//        List<Human> humans = model.getHumans();
//        Iterator<Human> iterator = humans.iterator();
//        while (iterator.hasNext()) {
//            Human human = iterator.next();
//            human.update(delta, model.getTime());
//        }
//    }
    public List<Animal> getAnimalsFromModel() {
        return model.getAnimals();
    }

    public List<Human> getHumanFromModel() {
        return model.getHumans();
    }

    public List<Ranger> getRangerFromModel() {
        ArrayList<Ranger> ret = new ArrayList<>();
        for (Human human : model.getHumans()) {
            if (human instanceof Ranger) {
                ret.add((Ranger) human);
            }
        }
        return ret;
    }

    public List<Poacher> getPoacherFromModel(){
        ArrayList<Poacher> po = new ArrayList<>();
        for(Human human : model.getHumans()){
            if(human instanceof Poacher){
                po.add((Poacher) human);
            }
        }
        return po;
    }

    public List<Animal> getHerbivorous() {
        return getAnimalsFromModel().stream()
            .filter(a -> a instanceof Herbivorous)
            .collect(Collectors.toList());
    }

    public List<Animal> getCarnivores() {
        return getAnimalsFromModel().stream()
            .filter(a -> a instanceof Carnivores)
            .collect(Collectors.toList());
    }

    public void updateHumans(float delta) {
        List<Human> humans = model.getHumans();
        Iterator<Human> iterator = humans.iterator();
        while (iterator.hasNext()) {
            Human human = iterator.next();
            human.update(delta, model.getTime());
        }
    }

    public void updateJeeps(float delta) {
        List<Jeep> jeeps = model.getJeeps();
        Iterator<Jeep> iterator = jeeps.iterator();
        while (iterator.hasNext()) {
            Jeep jeep = iterator.next();
            jeep.update(delta, model.getTime());
        }
    }

    public List<Jeep> getJeepsFromModel() {
        return model.getJeeps();
    }

    public static synchronized GameController getInstance(GameModel model) {
        if (instance == null) {
            instance = new GameController(model);
        }
        return instance;
    }

    public Capital getCapital() {
        return model.getCapital();
    }

    public String getSafariName() {
        return model.getPlayer().getName();
    }

    public Time getTime() {
        return model.getTime();
    }

    public void playerChooseSpeed(Time.Speed speed) {
        model.getTime().advanceTime(speed);
    }

    public void update(float delta) {
        // Update real-time based time
        model.getTime().updateCurrentTime();

        // Example: Advance 1 hour in-game each second
        if ((int) (delta * 1000) % 1000 == 0) {
            model.getTime().advanceTime(Time.Speed.HOUR);
        }

        // monthly action
        if (model.getTime().hasMonthChanged()) {
            this.model.updateCurrentCarnivores();
            this.model.updateCurrentHerbivores();
            model.checkMonthlyWinCondition();
            model.PaidSalary();
        }
        model.checkLostCondition();
    }

    public int getMapSize() {
        return model.getMAP_SIZE();
    }

    private CheckBox selectedCheckBox;

    public void setSlectedTiles(CheckBox checkBox) {
        this.selectedCheckBox = checkBox;
    }

    public CheckBox getSelectedCheckBox() {
        return selectedCheckBox;
    }

    private CheckBox InventoryCheckBox;

    public void setInventoryCheckBox(CheckBox checkBox) {
        this.InventoryCheckBox = checkBox;
    }

    public CheckBox getInventoryCheckBox() {
        return this.InventoryCheckBox;
    }

    //    public void addAnimal() {
//        this.model.addAnimal();
//    }
    public void addSheep(float x, float z) {
        this.model.addSheep(x,z);
    }

    public void addLion(float x, float z) {
        this.model.addLion(x,z);
    }

    public void addCheetah(float x, float z) {
        this.model.addCheetah(x,z);
    }

    public void addRanger(float x, float y, GameController controller) {
        this.model.addRanger(x, y, controller);
    }

    public void addPoacher(float x, float y, GameController controller, Animal animal) {

        int col = r.nextInt(GameModel.MAP_SIZE * 10);
        int row = r.nextInt(GameModel.MAP_SIZE * 10);

        this.model.addPoacher(col, row, controller, animal);
    }

    public void addJeep() {
        this.model.addJeep();
    }

    public void addHourse(float x, float z) {
        this.model.addHourse(x,z);
    }

    public GameModel getGameModel() {
        return model;
    }

    public Terrain getCurrentTerrain(Animal animal) {
        Vector3 position = animal.getPosition();
        int x = (int) (position.x / 10);
        int z = (int) (position.z / 10);

        // ゲームマップからその位置の地形を取得
        return model.getTerrain(x, z);
    }

    public AnimalView getAnimalView(Animal animal) {
        return animalFactory.createAnimal(animal);
    }

    public void RemoveInventoryCount(Class<? extends Terrain> clazz) {
        this.model.getInventory().removeEntity(clazz);
        //System.out.println(model.getInventory().getCount(clazz) + "デバッグ");

    }

    public void removeAnimal(Animal animal) {
        model.getAnimals().remove(animal);
    }

    public void removeHuman(Human human) {
        model.getHumans().remove(human);
    }

    public Ranger getClosestRangerTo(Animal animal) {
        List<Ranger> rangers = this.model.getRanger();
        Ranger closest = null;
        float minDist = Float.MAX_VALUE;

        if (rangers.isEmpty() || animal == null) {
            return null;
        }
        for (Ranger r : rangers) {

            Vector3 tmp = r.getPosition();
            Vector3 tmp2 = animal.getPosition();

            float dist = r.getPosition().dst2(animal.getPosition());
            if (dist < minDist && r.getState() != Ranger.RangerState.MISSION) {
                minDist = dist;
                closest = r;
            }
        }
        return closest;
    }

    public void setNeedUpdate(boolean l) {
        this.needUpdate = l;
    }

    public boolean getNeedUpdate() {
        return this.needUpdate;
    }

    public int getAttractiveness() {
        return this.model.getAttractiveness();
    }

    public List<Tourist> getWaitingTourist() {
        return this.model.getTourist();
    }

    public void updateTourist() {
        this.model.updateWaitingTourists();
    }

    public boolean isWon() {
        return model.getState() == GameState.WON;
    }

    public boolean isLost() {
        return model.getState() == GameState.LOST;
    }

    public int getMonthlyVisitorCount() {
        return model.getMonthlyVisitorCount();
    }

    public void reset(GameModel newModel) {
        this.model = newModel;
    }

    public void init(GameModel model) {
        if (this.model == null) {
            this.model = model;
        }
    }
}
