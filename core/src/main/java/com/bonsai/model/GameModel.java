package com.bonsai.model;

import com.bonsai.controller.GameController;
import com.bonsai.model.interfaces.MapChangeObserver;

import java.util.*;
import java.util.stream.Collectors;

public class GameModel {
    static public final int MAP_SIZE = 20;
    private Terrain[][] map;
    private int entrancePosX;
    private int entrancePosY;
    private int exitPosX;
    private int exitPosY;
    private List<Animal> animals;
    private List<Jeep> jeeps;
    private List<Human> humans;
    private Player player;
    private Capital capital;
    private Time time;
    private Inventory inventory;
    private List<MapChangeObserver> observers = new ArrayList<>();
    private JeepManager jeepManager;
    private final int RangerSalary = 1000;
    private boolean salaryPaidThisMonth = false;
    private Random r;
    private List<Human> humansToRemove = new ArrayList<>();
    private List<Animal> animalToRemove = new ArrayList<>();

    private List<Tourist> waitingTourist;
    private int maxWaitingTourist;
    private int attractiveness;
    private int lastTouristUpdateDay = -1;

    private GameThresholds thresholds;
    private final int MinHerbivores = 1;
    private final int MinCarnivores = 1;
    private final int MinTourists = 1;
    private final int MinCapital = 100000;

    private int currentHerbivores;
    private int currentCarnivores;
    private int monthlyVisitorCount;
    private Queue<Boolean> successStreak;

    private int requiredMonths;

    private GameState state;
    private MapGenerator mapGenerator;

    private boolean hadAnimals = false; // This is used to avoid the immediate lost after the game starts

    public GameModel(Player player, Capital capital, Time time, Inventory inventory, int requiredMonths) {
        this.player = player;
        this.capital = capital;
        this.time = time;
        this.inventory = inventory;
        this.animals = new ArrayList<>();
        this.jeeps = new ArrayList<>();
        this.humans = new ArrayList<>();
        this.map = new Terrain[MAP_SIZE][MAP_SIZE];

        entrancePosX = 0;
        entrancePosY = 5;
        exitPosX = 19;
        exitPosY = 5;

        mapGenerator = new MapGenerator(this);

        initilizeMap();
        jeepManager = new JeepManager(this);
        addMapObserver(jeepManager);
        r = new Random();

        waitingTourist = new ArrayList<>();

        thresholds = new GameThresholds(MinHerbivores, MinCarnivores, MinTourists, MinCapital);
        currentCarnivores = 0;
        currentHerbivores = 0;
        monthlyVisitorCount = 0;

        successStreak = new LinkedList<>();

        this.requiredMonths = requiredMonths;

        this.state = GameState.RUNNING;
    }

    public void initilizeMap() {
        this.map = mapGenerator.getTemplate();
    }

    //    public void addAnimal(){
//        animals.add(new HerbivorousA(0,0,0, time));
//    }
    public void addSheep(float tileX, float tileZ) {
        Animal sheep = new Sheep(tileX, 0, tileZ, time);
        animals.add(sheep);
        sheep.setGameModel(this);
        calculateAttractivenessScore();
        hadAnimals = true;
    }

    public void addLion(float tileX, float tileZ) {
        Animal lion = new Lion(tileX, 0, tileZ, time);
        animals.add(lion);
        lion.setGameModel(this);
        calculateAttractivenessScore();
        hadAnimals = true;
    }

    public void addCheetah(float tileX, float tileZ) {
        Animal cheetah = new Cheetah(tileX, 0, tileZ, time);
        animals.add(cheetah);
        cheetah.setGameModel(this);
        calculateAttractivenessScore();
        hadAnimals = true;
    }

    public void addHourse(float tileX, float tileZ) {
        Animal hourse = new Horse(tileX, 0, tileZ, time);
        animals.add(hourse);
        hourse.setGameModel(this);
        calculateAttractivenessScore();
        hadAnimals = true;
    }

    public void addRanger(float x, float y, GameController controller) {
        humans.add(new Ranger(x, 0, y, controller));
    }

    public void addPoacher(float x, float y, GameController controller, Animal animal) {
        humans.add(new Poacher(x, 0, y, controller, animal));
    }

    public void addJeep() {
        Jeep jeep = jeepManager.createJeep();
        jeeps.add(jeep);
    }


    public void addPondToMap(int x, int y) {
        map[x][y] = new Pond(x, y);
        map[x + 1][y] = new Pond(x + 1, y);
        map[x][y + 1] = new Pond(x, y + 1);
        map[x + 1][y + 1] = new Pond(x + 1, y + 1);
    }

    public Terrain[][] getMap() {
        return map;
    }

    public void setTerrain(int x, int z, Terrain terrain) {
        map[z][x] = terrain;
        notifyMapChanged();
    }

    public Terrain getTerrain(int x, int y) {
        return map[y][x];
    }

    public Capital getCapital() {
        return capital;
    }

    public Player getPlayer() {
        return player;
    }

    public Time getTime() {
        return time;
    }

    public int getMAP_SIZE() {
        return MAP_SIZE;
    }

    public int getEntrancePosX() {
        return entrancePosX;
    }
    public void setEntrancePosX(int entrancePosX) {
        this.entrancePosX = entrancePosX;
    }
    public int getEntrancePosY() {
        return entrancePosY;
    }
    public int getExitPosX(){
        return exitPosX;
    }

    public int getExitPosY(){
        return exitPosY;
    }

    public void setExitPosX(int x){
        this.exitPosX = x;
    }

    public void setExitPosY(int y){
        this.exitPosY = y;
    }


    public Inventory getInventory() {
        return this.inventory;
    }

    public void setEntrancePosY(int entrancePosY) {
        this.entrancePosY = entrancePosY;
    }

    public List<Animal> getAnimals() {
        return animals;
    }

    public List<Human> getHumans() {
        return humans;
    }

    public List<Jeep> getJeeps() {
        return jeeps;
    }

    public void addMapObserver(MapChangeObserver observer) {
        observers.add(observer);
    }

    private void notifyMapChanged() {
        for (MapChangeObserver observer : observers) {
            observer.onMapChanged();
        }
    }

    public List<Ranger> getRanger() {
        return humans.stream()
            .filter(h -> h instanceof Ranger)
            .map(h -> (Ranger) h)
            .collect(Collectors.toList());
    }

    public void PaidSalary() {

        this.capital.subtractMoney(getRanger().size() * this.RangerSalary);
        System.out.println("paid");

    }

    public void calculateAttractivenessScore() {
        long speciesVariety = animals.stream()
            .map(animal -> animal.getClass().getSimpleName())
            .distinct()
            .count();

        int totalAnimals = animals.size();
        attractiveness = (int) (speciesVariety * 10 + totalAnimals);
    }

    public int getAttractiveness() {
        return attractiveness;
    }

    public void updateWaitingTourists() {
        int currentDay = time.getElapsedDays();

        if (currentDay != lastTouristUpdateDay) {
            calculateAttractivenessScore();

            int score = getAttractiveness();
            int newTourists = score / 20;

            for (int i = 0; i < newTourists; i++) {
                waitingTourist.add(new Tourist());
            }

            lastTouristUpdateDay = currentDay;
        }
    }

    public Animal getRandomAnimal() {
        List<Animal> freeAnimals = animals.stream()
            .filter(a -> a.getPoacher() == null)
            .collect(Collectors.toList());

        if (freeAnimals.isEmpty()) {
//            System.out.println("No free animals available.");
            return null;
        }

        int num = r.nextInt(freeAnimals.size());
        return freeAnimals.get(num);
    }

    public void addHumanToRemove(Human human) {
        humansToRemove.add(human);
    }

    public void addAnimalToRemove(Animal animal){
        animalToRemove.add(animal);
    }

    public List<Animal> getAnimalToRemove(){
        return this.animalToRemove;
    }

    public List<Human> getHumansToRemove() {
        return this.humansToRemove;
    }

    public List<Tourist> getTourist() {
        return waitingTourist;
    }

    public void assignTouristsToJeep(Jeep jeep) {
        int seats = 4;
        int available = Math.min(seats, waitingTourist.size());

        List<Tourist> boardingTourists = new ArrayList<>();

        for (int i = 0; i < available; i++) {
            boardingTourists.add(waitingTourist.remove(0));
        }

        jeep.setPassengers(boardingTourists);
    }

    public void updateCurrentHerbivores() {
        int num = 0;
        for (Animal animal : animals) {
            if (animal instanceof Herbivorous) {
                num += 1;
            }
        }
        this.currentHerbivores = num;
    }

    public void updateCurrentCarnivores() {
        int num = 0;
        for (Animal animal : animals) {
            if (animal instanceof Carnivores) {
                num += 1;
            }
        }
        this.currentCarnivores = num;
    }

    public void setCurrentTourists() {
    }

    public void checkMonthlyWinCondition() {
        boolean passed = currentHerbivores >= thresholds.minHerbivores &&
            currentCarnivores >= thresholds.minCarnivores &&
            monthlyVisitorCount >= thresholds.minTourists &&
            capital.getMoney() >= thresholds.minCapital;

        successStreak.add(passed);
        if (successStreak.size() > requiredMonths) {
            successStreak.poll();
        }

        if (successStreak.size() == requiredMonths &&
            successStreak.stream().allMatch(b -> b)) {
            triggerWin();
        }

        resetMonthlyVisitorCount();
    }

    public void triggerWin() {
        state = GameState.WON;
    }

    public void checkLostCondition() {
        boolean allAnimalsDead = animals.isEmpty();

        if (capital.isBankrupt() || (hadAnimals && allAnimalsDead)) {
            triggerLost();
        }
    }

    public void triggerLost() {
        state = GameState.LOST;
    }

    public GameState getState() {
        return state;
    }

    public void incrementVisitorCount(int count) {
        monthlyVisitorCount += count;
    }

    public int getMonthlyVisitorCount() {
        return monthlyVisitorCount;
    }

    public void resetMonthlyVisitorCount() {
        monthlyVisitorCount = 0;
    }
}
