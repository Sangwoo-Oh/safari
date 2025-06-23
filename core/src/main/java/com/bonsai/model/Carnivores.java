package com.bonsai.model;

import java.util.List;

import static com.bonsai.model.GameModel.MAP_SIZE;

public abstract class Carnivores extends Animal {
    private GameModel model;
    private final float HUNGER_VALUE = 200;
    protected float hunger = HUNGER_VALUE;

    public Carnivores(float x, float y, float z, Time time) {
        super(x, y, z, time);
//        this.model = model;
    }

    @Override
    public void update(float delta, Time time, List<Animal> allAnimals) {
        super.update(delta, time, allAnimals);

        if (this.hungry) {
            System.out.println("lion is hungry");

            List<Animal> nearbyAnimals = allAnimals;
            Sheep targetHerbivore = null;

            // ループの中では削除しない
            for (Animal animal : nearbyAnimals) {
                if (animal instanceof Sheep) {
                    if(((int) this.getX() / MAP_SIZE) - 1 <= (int) animal.getX() / MAP_SIZE && (int) animal.getX() / MAP_SIZE <= ((int) this.getX() / MAP_SIZE) + 1 && ((int) this.getZ() / MAP_SIZE) - 1 <= (int) animal.getZ() / MAP_SIZE && (int) animal.getZ() / MAP_SIZE <= ((int) this.getZ() / MAP_SIZE) + 1){
                        targetHerbivore = (Sheep) animal;
                        eatHerbivore(animal, time);
//                        System.out.println("sheepの座標: (" + (int) animal.getX() / MAP_SIZE + ", " + (int) animal.getZ() / MAP_SIZE + ")");
//                        System.out.println("lionの座標: (" + (int) this.getX() / MAP_SIZE + ", " + (int) this.getZ() / MAP_SIZE + ")");
                        break;
                    }
                }
            }
        }
    }


    private void eatHerbivore(Animal animal, Time time) {
        animal.die();
        System.out.println("lion ate sheep");
        fillHunger(time);
    }

    private void fillHunger(Time time) {
        this.hungerTime = time.getTotalElapsedGameHours() / 24;
        this.hungry = false;
    }

    @Override
    public void getHungry(Time time) {
        if(age / lifespan <= 0.3){
            gettingHungry(time, 1.5f);
        }else if(0.3 < age / lifespan && age / lifespan <= 0.6){
            gettingHungry(time, 2f);
        }else if(age / lifespan > 0.6){
            gettingHungry(time, 3.5f);
        }
    }

    private void gettingHungry(Time time, float x){
        float elapsedTime = time.getTotalElapsedGameHours() / 24f - this.hungerTime;
        if (elapsedTime >= this.hunger) {
            this.die();
        } else if (elapsedTime >= (float)this.hunger / x) {
            this.hungry = true;
        }
    }
}
